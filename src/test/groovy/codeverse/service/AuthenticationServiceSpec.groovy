package codeverse.service

import codeverse.com.web_be.dto.request.AuthenRequest.ChangePasswordRequest
import codeverse.com.web_be.dto.request.AuthenRequest.LogoutRequest
import codeverse.com.web_be.dto.request.AuthenRequest.SignUpRequest
import codeverse.com.web_be.dto.request.AuthenRequest.AuthenticationRequest
import codeverse.com.web_be.dto.response.AuthenResponse.AuthenticationResponse
import codeverse.com.web_be.entity.User
import codeverse.com.web_be.enums.InstructorStatus
import codeverse.com.web_be.enums.UserRole
import codeverse.com.web_be.exception.AppException
import codeverse.com.web_be.exception.ErrorCode
import codeverse.com.web_be.repository.InvalidatedTokenRepository
import codeverse.com.web_be.repository.UserRepository
import codeverse.com.web_be.service.AuthenService.AuthenticationService
import codeverse.com.web_be.service.EmailService.EmailServiceSender
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService
import codeverse.com.web_be.service.GoogleService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Unroll

class AuthenticationServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def invalidatedTokenRepository = Mock(InvalidatedTokenRepository)
    def emailService = Mock(EmailServiceSender)
    def firebaseStorageService = Mock(FirebaseStorageService)
    def googleService = Mock(GoogleService)

    def service = new AuthenticationService(
            googleService,
            userRepository,
            invalidatedTokenRepository,
            firebaseStorageService,
            emailService
    )

    def setup() {
        service.SIGNER_KEY = "1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij"
        service.VALID_DURATION = 3600
        service.REFRESHABLE_DURATION = 7200
    }

    // ====== success cases ======
    @Unroll
    def "No: #no → authenticate success with email=#email"() {
        given:
        def encoder = new BCryptPasswordEncoder(10)
        def user = User.builder()
                .id(1L)
                .username(email)
                .password(encoder.encode(password))
                .isVerified(true)
                .isDeleted(false)
                .role(UserRole.LEARNER)
                .build()
        userRepository.findByUsername(email) >> Optional.of(user)

        when:
        def result = service.authenticate(new AuthenticationRequest(username: email, password: password))

        then:
        result.authenticated
        result.token != null
        result.refreshToken != null

        where:
        no        | email                       | password
        "UTCID01" | "trieulqde160447@gmail.com" | "Password0@"
        "UTCID09" | "trieulqde160447@gmail.com" | "Password0@"
    }

    // ====== failure cases ======
    @Unroll
    def "No: #no → authenticate fail with email=#email password=#password → expect #expectedException"() {
        given:
        def encoder = new BCryptPasswordEncoder(10)
        if (mockUser != null) {
            userRepository.findByUsername(email) >> Optional.of(mockUser)
        } else {
            userRepository.findByUsername(email) >> Optional.empty()
        }

        when:
        service.authenticate(new AuthenticationRequest(username: email, password: password))

        then:
        def ex = thrown(AppException)
        ex.errorCode == expectedException

        where:
        no        | email                       | password     | mockUser                                                                                                                                                                                                                       || expectedException
        "UTCID02" | "invalidUser@gmail.com"     | "Password0@" | null                                                                                                                                                                                                                           || ErrorCode.USER_NOT_EXISTED
        "UTCID03" | "trieulqde160447"           | "Password0@" | User.builder().username("trieulqde160447").password(new BCryptPasswordEncoder().encode("Password1@")).isVerified(true).isDeleted(false).role(UserRole.LEARNER).build()                                                         || ErrorCode.UNAUTHENTICATED
        "UTCID04" | ""                          | "Password0@" | null                                                                                                                                                                                                                           || ErrorCode.USER_NOT_EXISTED
        "UTCID05" | "trieulqde160447@gmail.com" | ""           | User.builder().username("trieulqde160447@gmail.com").password(new BCryptPasswordEncoder().encode("Password0@")).isVerified(true).isDeleted(false).role(UserRole.LEARNER).build()                                               || ErrorCode.UNAUTHENTICATED
        "UTCID06" | "trieulqde160447@gmail.com" | "Password0@" | User.builder().username("trieulqde160447@gmail.com").password(new BCryptPasswordEncoder().encode("Password0@")).isVerified(false).isDeleted(false).role(UserRole.LEARNER).build()                                              || ErrorCode.UN_VERIFY_EMAIL
        "UTCID07" | "trieulqde160447@gmail.com" | "Password0@" | User.builder().username("trieulqde160447@gmail.com").password(new BCryptPasswordEncoder().encode("Password0@")).isVerified(true).isDeleted(true).role(UserRole.LEARNER).build()                                                || ErrorCode.USER_BANNED
        "UTCID08" | "trieulqde160447@gmail.com" | "Password0@" | User.builder().username("trieulqde160447@gmail.com").password(new BCryptPasswordEncoder().encode("Password0@")).isVerified(true).isDeleted(false).role(UserRole.INSTRUCTOR).instructorStatus(InstructorStatus.PENDING).build() || ErrorCode.INSTRUCTOR_NOT_ACTIVE
    }

    // ====== success cases ======
    @Unroll
    def "No: #no → register success with role=#role email=#email"() {
        given:
        userRepository.findByUsername(email) >> Optional.empty()
        firebaseStorageService.uploadImage(_ as MultipartFile) >> "url://image"
        emailService.sendVerificationEmail(_ as String, _ as String) >> {}

        def teachingFile = hasTeachingFile ? Mock(MultipartFile) {
            getOriginalFilename() >> "teachingCert.png"
            isEmpty() >> false
        } : null

        def qrFile = Mock(MultipartFile) {
            getOriginalFilename() >> "dummyQr.png"
            isEmpty() >> false
        }

        def req = SignUpRequest.builder()
                .username(email)
                .password(password)
                .name("Test User")
                .role(role)
                .phoneNumber(phone)
                .teachingCredentials(teachingFile)
                .qrCodeUrl(qrFile)
                .educationalBackground("Bachelor")
                .build()

        when:
        AuthenticationResponse result = service.authenticateSignup(req)

        then:
        result.authenticated
        result.token != null

        where:
        no        | role        | email                 | password     | phone       | hasTeachingFile
        "UTCID01" | "LEARNER"   | "learner@test.com"    | "Password0@" | null        | false
        "UTCID05" | "INSTRUCTOR"| "instructor@test.com" | "Password0@" | "0123456789"| true
    }

    // ====== failure cases ======
    @Unroll
    def "No: #no → register fail with role=#role email=#email → expect #expectedException"() {
        given:
        if (alreadyRegistered) {
            userRepository.findByUsername(email) >> Optional.of(new User())
        } else {
            userRepository.findByUsername(email) >> Optional.empty()
        }

        firebaseStorageService.uploadImage(_ as MultipartFile) >> {
            if (invalidFileType) throw new AppException(ErrorCode.INVALID_CREDENTIALS)
            return "url://image"
        }

        emailService.sendVerificationEmail(_ as String, _ as String) >> {}

        def teachingFile = hasTeachingFile ? (invalidFileType
                ? Mock(MultipartFile) {
            getOriginalFilename() >> "badFile.txt"
            isEmpty() >> false
        }
                : Mock(MultipartFile) {
            getOriginalFilename() >> "teachingCert.png"
            isEmpty() >> false
        }) : null

        def qrFile = Mock(MultipartFile) {
            getOriginalFilename() >> "dummyQr.png"
            isEmpty() >> false
        }

        def req = SignUpRequest.builder()
                .username(email)
                .password(password)
                .name("Test User")
                .role(role)
                .phoneNumber(phone)
                .teachingCredentials(teachingFile)
                .qrCodeUrl(qrFile)
                .educationalBackground("Bachelor")
                .build()

        when:
        service.authenticateSignup(req)

        then:
        def ex = thrown(AppException)
        ex.errorCode == expectedException

        where:
        no        | role        | email                  | password     | phone        | hasTeachingFile | alreadyRegistered | invalidFileType || expectedException
        "UTCID02" | "LEARNER"   | "invalidEmail"         | "Password0@" | null         | false           | false             | false           || ErrorCode.INVALID_EMAIL
        "UTCID03" | "LEARNER"   | "learner@test.com"     | "Password0@" | null         | false           | true              | false           || ErrorCode.USER_EXISTED
        "UTCID04" | "LEARNER"   | "weak@test.com"        | "123"        | null         | false           | false             | false           || ErrorCode.INVALID_PASSWORD
        "UTCID06" | "INSTRUCTOR"| "missing@inst.com"     | "Password0@" | "0123456789" | false           | false             | false           || ErrorCode.INVALID_CREDENTIALS
        "UTCID07" | "INSTRUCTOR"| "invalidfile@inst.com" | "Password0@" | "0123456789" | true            | false             | true            || ErrorCode.INVALID_CREDENTIALS
        "UTCID08" | "INSTRUCTOR"| "nophone@inst.com"     | "Password0@" | ""           | true            | false             | false           || ErrorCode.INVALID_PHONE
        "UTCID09" | "INSTRUCTOR"| "instExist@test.com"   | "Password0@" | "0123456789" | true            | true              | false           || ErrorCode.USER_EXISTED
    }

    // ====== success cases ======
    @Unroll
    def "No: #no → google login success with idToken=#idToken (case: #caseDesc)"() {
        given:
        def payload = [email: email, name: "Google User", picture: "avatar.png"]
        googleService.verifyToken(idToken) >> payload

        if (userExists) {
            def user = User.builder()
                    .id(1L)
                    .username(email)
                    .password("GOOGLE_CODEVERSExxxx")
                    .role(UserRole.LEARNER)
                    .isVerified(true)
                    .isDeleted(false)
                    .build()
            userRepository.findByUsername(email) >> Optional.of(user)
        } else {
            userRepository.findByUsername(email) >> Optional.empty()
            userRepository.save(_ as User) >> { args -> args[0] }
        }

        when:
        AuthenticationResponse result =
                service.authenticateGoogleLogin(new AuthenticationRequest(username: idToken, password: null))

        then:
        result.authenticated
        result.token != null
        result.refreshToken != null

        where:
        no        | caseDesc                | idToken        | email               | userExists
        "UTCID01" | "Valid, user exists"    | "validToken1"  | "user1@gmail.com"   | true
        "UTCID02" | "Valid, user not exist" | "validToken2"  | "newuser@gmail.com" | false
    }

    // ====== failure cases ======
    @Unroll
    def "No: #no → google login fail with idToken=#idToken → expect #expectedException"() {
        given:
        if (exceptionCase == "INVALID") {
            googleService.verifyToken(idToken) >> { throw new RuntimeException("Invalid token") }
        } else if (exceptionCase == "EMAIL_REGISTERED") {
            googleService.verifyToken(idToken) >> [email: email, name: "Exist User", picture: "avatar.png"]
            def user = User.builder()
                    .username(email)
                    .password("normalPasswordHash") // password-based account
                    .role(UserRole.LEARNER)
                    .isVerified(true)
                    .isDeleted(false)
                    .build()
            userRepository.findByUsername(email) >> Optional.of(user)
        } else if (exceptionCase == "USER_BANNED") {
            googleService.verifyToken(idToken) >> [email: email, name: "Banned User", picture: "avatar.png"]
            def user = User.builder()
                    .username(email)
                    .password("GOOGLE_CODEVERSExxxx")
                    .role(UserRole.LEARNER)
                    .isVerified(true)
                    .isDeleted(true) // banned
                    .build()
            userRepository.findByUsername(email) >> Optional.of(user)
        }

        when:
        service.authenticateGoogleLogin(new AuthenticationRequest(username: idToken, password: null))

        then:
        def ex = thrown(AppException)
        ex.errorCode == expectedException

        where:
        no        | idToken        | email                 | exceptionCase       || expectedException
        "UTCID05" | "validToken"   | "existPwd@gmail.com"  | "EMAIL_REGISTERED"  || ErrorCode.EMAIL_REGISTERED_WITH_PASSWORD
        "UTCID06" | "validToken"   | "banned@gmail.com"    | "USER_BANNED"       || ErrorCode.USER_BANNED
    }

    // ====== success case ======
    @Unroll
    def "No: #no → forgot password success with email=#email"() {
        given:
        def user = User.builder()
                .id(1L)
                .username(email)
                .password(new BCryptPasswordEncoder(10).encode("oldPassword"))
                .isVerified(true)
                .isDeleted(false)
                .role(UserRole.LEARNER)
                .build()

        userRepository.findByUsername(email) >> Optional.of(user)
        userRepository.save(_ as User) >> { args -> args[0] }
        emailService.sendResetPasswordEmail(email, _ as String) >> {}

        def req = SignUpRequest.builder()
                .username(email)
                .build()

        when:
        service.authenticateResetPassword(req)

        then:
        1 * emailService.sendResetPasswordEmail(email, _ as String)

        where:
        no       | email
        "UTCID01"| "validuser@test.com"
    }

    // ====== failure cases ======
    @Unroll
    def "No: #no → forgot password fail with email=#email → expect #expectedException"() {
        given:
        if (caseType == "NOT_EXIST") {
            userRepository.findByUsername(email) >> Optional.empty()
        } else if (caseType == "GOOGLE_USER") {
            def googleUser = User.builder()
                    .username(email)
                    .password("GOOGLE_CODEVERSE_xxx")
                    .isVerified(true)
                    .isDeleted(false)
                    .role(UserRole.LEARNER)
                    .build()
            userRepository.findByUsername(email) >> Optional.of(googleUser)
        } else {
            // empty or invalid email
            userRepository.findByUsername(email) >> Optional.empty()
        }

        def req = SignUpRequest.builder()
                .username(email)
                .build()

        when:
        service.authenticateResetPassword(req)

        then:
        def ex = thrown(AppException)
        ex.errorCode == expectedException

        where:
        no        | email                | caseType       || expectedException
        "UTCID02" | "notfound@test.com"  | "NOT_EXIST"    || ErrorCode.USER_NOT_EXISTED
        "UTCID03" | "google@test.com"    | "GOOGLE_USER"  || ErrorCode.RESET_PASSWORD_NOT_SUPPORTED_FOR_GOOGLE
        "UTCID04" | ""                   | "INVALID"      || ErrorCode.USER_NOT_EXISTED
        "UTCID05" | "invalidEmail"       | "INVALID"      || ErrorCode.USER_NOT_EXISTED
    }

    // ====== success case ======
    @Unroll
    def "No: #no → change password success for user #username"() {
        given:
        def encoder = new BCryptPasswordEncoder(10)
        def user = User.builder()
                .id(1L)
                .username(username)
                .password(encoder.encode(oldPassword))
                .role(UserRole.LEARNER)
                .isVerified(true)
                .isDeleted(false)
                .build()

        userRepository.findByUsername(username) >> Optional.of(user)
        userRepository.save(_ as User) >> { args -> args[0] }

        def req = ChangePasswordRequest.builder()
                .username(username)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build()

        when:
        AuthenticationResponse result = service.authenticateChangePassword(req)

        then:
        result.authenticated
        result.token != null
        result.refreshToken != null

        where:
        no       | username          | oldPassword   | newPassword
        "UTCID01"| "valid@test.com"  | "OldPass123!" | "NewPass123!"
    }

    // ====== failure cases ======
    @Unroll
    def "No: #no → change password fail for user #username → expect #expectedException"() {
        given:
        def encoder = new BCryptPasswordEncoder(10)
        def user

        if (caseType == "GOOGLE_USER") {
            user = User.builder()
                    .username(username)
                    .password("GOOGLE_CODEVERSE_xxx")
                    .role(UserRole.LEARNER)
                    .isVerified(true)
                    .isDeleted(false)
                    .build()
        } else if (caseType == "OLD_PASSWORD_INCORRECT") {
            user = User.builder()
                    .username(username)
                    .password(encoder.encode("CorrectOldPass"))
                    .role(UserRole.LEARNER)
                    .isVerified(true)
                    .isDeleted(false)
                    .build()
        } else if (caseType == "NEW_PASSWORD_WEAK") {
            user = User.builder()
                    .username(username)
                    .password(encoder.encode("OldPass123!"))
                    .role(UserRole.LEARNER)
                    .isVerified(true)
                    .isDeleted(false)
                    .build()
        } else if (caseType == "PASSWORD_SAME_AS_OLD") {
            user = User.builder()
                    .username(username)
                    .password(encoder.encode("SamePass123"))
                    .role(UserRole.LEARNER)
                    .isVerified(true)
                    .isDeleted(false)
                    .build()
        } else if (caseType == "NEW_PASSWORD_NULL") {
            user = User.builder()
                    .username(username)
                    .password(encoder.encode("OldPass123!"))
                    .role(UserRole.LEARNER)
                    .isVerified(true)
                    .isDeleted(false)
                    .build()
        }

        userRepository.findByUsername(username) >> Optional.of(user)

        def req = ChangePasswordRequest.builder()
                .username(username)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build()

        when:
        service.authenticateChangePassword(req)

        then:
        def ex = thrown(AppException)
        ex.errorCode == expectedException

        where:
        no       | username            | oldPassword       | newPassword     | caseType                 || expectedException
        "UTCID02"| "google@test.com"   | "anyPass"         | "NewPass123!"   | "GOOGLE_USER"            || ErrorCode.CHANGE_PASSWORD_NOT_SUPPORTED_FOR_GOOGLE
        "UTCID03"| "wrongold@test.com" | "WrongPass"       | "NewPass123!"   | "OLD_PASSWORD_INCORRECT" || ErrorCode.OLD_PASSWORD_INCORRECT
        "UTCID04"| "weak@test.com"     | "OldPass123!"     | "123"           | "NEW_PASSWORD_WEAK"      || ErrorCode.INVALID_PASSWORD
        "UTCID05"| "same@test.com"     | "SamePass123"     | "SamePass123"   | "PASSWORD_SAME_AS_OLD"   || ErrorCode.PASSWORD_SAME_AS_OLD
        "UTCID06"| "nullnew@test.com"  | "OldPass123!"     | null            | "NEW_PASSWORD_NULL"      || ErrorCode.INVALID_PASSWORD
    }

    // ====== LOGOUT ======
    def "Logout success → token saved into invalidatedTokenRepository"() {
        given:
        def user = User.builder().id(1L).username("user@test.com").role(UserRole.LEARNER).build()
        def token = service.generateToken(user, true)

        when:
        service.logout(new LogoutRequest(token: token))

        then:
        1 * invalidatedTokenRepository.save(_)
    }

    // ====== REFRESH TOKEN ======
    def "Refresh token success → return new AuthenticationResponse"() {
        given:
        def user = User.builder().id(2L).username("refresh@test.com").role(UserRole.LEARNER).build()
        def refreshToken = service.generateToken(user, true)

        userRepository.findByUsername("refresh@test.com") >> Optional.of(user)

        when:
        AuthenticationResponse resp = service.refreshToken(refreshToken)

        then:
        resp.authenticated
        resp.token != null
        resp.refreshToken != null
    }

    def "Refresh token valid but user not exist → throw UNAUTHENTICATED"() {
        given:
        def user = User.builder().id(3L).username("ghost@test.com").role(UserRole.LEARNER).build()
        def refreshToken = service.generateToken(user, true)

        userRepository.findByUsername("ghost@test.com") >> Optional.empty()

        when:
        service.refreshToken(refreshToken)

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.UNAUTHENTICATED
    }
}
