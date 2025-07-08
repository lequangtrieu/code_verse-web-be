package codeverse.com.web_be.service.AuthenService;

import codeverse.com.web_be.dto.request.AuthenRequest.*;
import codeverse.com.web_be.dto.response.AuthenResponse.AuthenticationResponse;
import codeverse.com.web_be.dto.response.AuthenResponse.IntrospectResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.InvalidatedToken;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.InstructorStatus;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.InvalidatedTokenRepository;
import codeverse.com.web_be.repository.UserRepository;
import codeverse.com.web_be.service.EmailService.EmailServiceSender;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.GoogleService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    private final GoogleService googleService;
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    FirebaseStorageService firebaseStorageService;

    private final EmailServiceSender emailService;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY));

        if (user.getIsVerified()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        user.setIsVerified(true);
        user.setVerificationToken(null);

        userRepository.save(user);
    }

    public UserResponse getUserByEmail(String email) {
        var user = userRepository.findByUsername(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .avatar(user.getAvatar())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (Boolean.FALSE.equals(user.getIsVerified())) {
            throw new AppException(ErrorCode.UN_VERIFY_EMAIL);
        }
        if(Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new AppException(ErrorCode.USER_BANNED);
        }
        if (user.getRole() == UserRole.INSTRUCTOR && !InstructorStatus.APPROVED.equals(user.getInstructorStatus())) {
            throw new AppException(ErrorCode.INSTRUCTOR_NOT_ACTIVE);
        }
        var token = generateToken(user, false);
        var refreshToken = generateToken(user, true);
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    public AuthenticationResponse authenticateGoogleLogin(AuthenticationRequest request) {
        try {
            String idToken = request.getUsername();

            if (idToken == null || idToken.isBlank()) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            var googlePayload = googleService.verifyToken(idToken);
            String email = googlePayload.getEmail();
            String name = (String) googlePayload.get("name");
            String avatarUrl = (String) googlePayload.get("picture");

            var userOptional = userRepository.findByUsername(email);
            User user;

            if (userOptional.isPresent()) {
                user = userOptional.get();

                if (user.getPassword() != null
                        && !user.getPassword().isBlank()
                        && !user.getPassword().startsWith("GOOGLE_CODEVERSE")) {
                    throw new AppException(ErrorCode.EMAIL_REGISTERED_WITH_PASSWORD);
                }

                if (user.getIsDeleted()) {
                    throw new AppException(ErrorCode.USER_BANNED);
                }

            } else {
                user = User.builder()
                        .username(email)
                        .name(name)
                        .avatar(avatarUrl)
                        .role(UserRole.LEARNER)
                        .password("GOOGLE_CODEVERSE" + UUID.randomUUID().toString())
                        .isVerified(true)
                        .build();
                userRepository.save(user);
            }

            var token = generateToken(user, false);
            var refreshToken = generateToken(user, true);

            return AuthenticationResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .username(email)
                    .authenticated(true)
                    .build();

        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            log.error("Unexpected error during Google login: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public void authenticateResetPassword(SignUpRequest request) throws MessagingException {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.getPassword().isBlank() && user.getPassword().startsWith("GOOGLE_CODEVERSE")) {
            throw new AppException(ErrorCode.RESET_PASSWORD_NOT_SUPPORTED_FOR_GOOGLE);
        }

        String newPassword = generateRandomPassword(10);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailService.sendResetPasswordEmail(user.getUsername(), newPassword);
    }

    public AuthenticationResponse authenticateSignup(SignUpRequest request) throws MessagingException {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String verificationToken = UUID.randomUUID().toString();
        UserRole role = determineUserRole(request);

        String teachingCredentials = null;
        if (request.getTeachingCredentials() != null && !request.getTeachingCredentials().isEmpty()) {
            teachingCredentials = firebaseStorageService.uploadImage(request.getTeachingCredentials());
        }

        User.UserBuilder userBuilder = User.builder()
                .username(request.getUsername())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .verificationToken(verificationToken)
                .isVerified(false)
                .isDeleted(false);

        if (role == UserRole.INSTRUCTOR) {
            userBuilder.phoneNumber(request.getPhoneNumber());
            userBuilder.teachingCredentials(teachingCredentials);
            userBuilder.educationalBackground(request.getEducationalBackground());
            userBuilder.instructorStatus(InstructorStatus.PENDING);
        }

        User newUser = userBuilder.build();

        userRepository.save(newUser);

        emailService.sendVerificationEmail(newUser.getUsername(), verificationToken);

        String token = generateToken(newUser, false);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public IntrospectResponse introspectToken(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken) throws ParseException, JOSEException {
        var signedJWT = verifyToken(refreshToken, true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        try {
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                System.out.println("Refresh token.");
            } else {
                throw e;
            }
        }

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var tokenNew = generateToken(user, false);
        var refreshTokenNew = generateToken(user, true);

        return AuthenticationResponse.builder()
                .token(tokenNew)
                .refreshToken(refreshTokenNew)
                .authenticated(true)
                .build();
    }

    private String generateToken(User user, boolean isRefresh) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date expiration = isRefresh
                ? new Date(Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("codeVerse.com")
                .issueTime(new Date())
                .expirationTime(expiration)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.getRole())
                .claim("userId", user.getId())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    public AuthenticationResponse  authenticateChangePassword(ChangePasswordRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getPassword() != null && user.getPassword().startsWith("GOOGLE_CODEVERSE")) {
            throw new AppException(ErrorCode.CHANGE_PASSWORD_NOT_SUPPORTED_FOR_GOOGLE);
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        String newPassword = request.getNewPassword();
        if (newPassword == null || newPassword.length() < 8) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        if (encoder.matches(newPassword, user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        var token = generateToken(user, false);
        var refreshToken = generateToken(user, true);

        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .authenticated(true)
                .build();
    }

    private UserRole determineUserRole(SignUpRequest request) {
        if ("INSTRUCTOR".equalsIgnoreCase(request.getRole())) {
            return UserRole.INSTRUCTOR;
        }
        return UserRole.LEARNER;
    }
}
