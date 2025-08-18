package codeverse.service

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

}
