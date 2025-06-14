//package codeverse.service
//
//import codeverse.com.web_be.dto.request.AuthenRequest.AuthenticationRequest
//import codeverse.com.web_be.dto.response.AuthenResponse.AuthenticationResponse
//import codeverse.com.web_be.exception.AppException
//import codeverse.com.web_be.exception.ErrorCode
//import codeverse.com.web_be.repository.UserRepository
//import codeverse.com.web_be.service.AuthenService.AuthenticationService
//import org.springframework.security.crypto.password.PasswordEncoder
//import spock.lang.Specification
//import spock.lang.Subject
//
//class AuthenticationServiceSpec extends Specification {
//
//    def userRepository = Mock(UserRepository)
//    def passwordEncoder = Mock(PasswordEncoder)
//
//    @Subject
//    def authenticationService = Spy(AuthenticationService, constructorArgs: [null, userRepository, null, null, null]) {
//        generateToken(_, _) >> "dummy-token"
//    }
//
//    def setup() {
//        authenticationService.setSignerKey("1TjXchw5FloESb63Kc+DFhTARvpWL4jUGCwfGWxuG5SIf/1y/LgJxHnMqaF6A/ij") // nếu SIGNER_KEY dùng setter hoặc public field
//        authenticationService.setValidDuration(3600L)
//        authenticationService.setRefreshableDuration(7200L)
//    }
//
////    def buildUser(Map overrides = [:]) {
////        new User(
////                username: overrides.get("username", "testuser"),
////                password: overrides.get("password", "encodedPass"),
////                isVerified: overrides.get("isVerified", true),
////                isDeleted: overrides.get("isDeleted", false),
////                setInstructorStatus: overrides.get("isActiveInstructor", true),
////                role: overrides.get("role", UserRole.INSTRUCTOR)
////        )
////    }
//
//    def "should throw USER_NOT_EXISTED when user not found"() {
//        given:
//        def request = new AuthenticationRequest(username: "invalid", password: "password")
//        userRepository.findByUsername("invalid") >> Optional.empty()
//
//        when:
//        authenticationService.authenticate(request)
//
//        then:
//        def ex = thrown(AppException)
//        ex.errorCode == ErrorCode.USER_NOT_EXISTED
//    }
//
//    def "should throw UNAUTHENTICATED when password mismatch"() {
//        given:
//        def user = buildUser()
//        def request = new AuthenticationRequest(username: user.username, password: "wrongpass")
//        userRepository.findByUsername(user.username) >> Optional.of(user)
//
//        PasswordEncoder encoder = Mock()
//        encoder.matches("wrongpass", "encodedPass") >> false
//        authenticationService.metaClass.passwordEncoder = encoder
//
//        when:
//        authenticationService.authenticate(request)
//
//        then:
//        def ex = thrown(AppException)
//        ex.errorCode == ErrorCode.UNAUTHENTICATED
//    }
//
//    def "should throw UN_VERIFY_EMAIL when user not verified"() {
//        given:
//        def user = buildUser(isVerified: false)
//        def request = new AuthenticationRequest(username: user.username, password: "password")
//        userRepository.findByUsername(user.username) >> Optional.of(user)
//
//        PasswordEncoder encoder = Mock()
//        encoder.matches("password", user.password) >> true
//        authenticationService.metaClass.passwordEncoder = encoder
//
//        when:
//        authenticationService.authenticate(request)
//
//        then:
//        def ex = thrown(AppException)
//        ex.errorCode == ErrorCode.UN_VERIFY_EMAIL
//    }
//
//    def "should throw USER_BANNED when user is deleted"() {
//        given:
//        def user = buildUser(isDeleted: true)
//        def request = new AuthenticationRequest(username: user.username, password: "password")
//        userRepository.findByUsername(user.username) >> Optional.of(user)
//
//        PasswordEncoder encoder = Mock()
//        encoder.matches("password", user.password) >> true
//        authenticationService.metaClass.passwordEncoder = encoder
//
//        when:
//        authenticationService.authenticate(request)
//
//        then:
//        def ex = thrown(AppException)
//        ex.errorCode == ErrorCode.USER_BANNED
//    }
//
//    def "should throw INSTRUCTOR_NOT_ACTIVE if instructor is inactive"() {
//        given:
//        def user = buildUser(isActiveInstructor: false)
//        def request = new AuthenticationRequest(username: user.username, password: "password")
//        userRepository.findByUsername(user.username) >> Optional.of(user)
//
//        PasswordEncoder encoder = Mock()
//        encoder.matches("password", user.password) >> true
//        authenticationService.metaClass.passwordEncoder = encoder
//
//        when:
//        authenticationService.authenticate(request)
//
//        then:
//        def ex = thrown(AppException)
//        ex.errorCode == ErrorCode.INSTRUCTOR_NOT_ACTIVE
//    }
//
//    def "should return token on successful authentication"() {
//        given:
//        def user = buildUser()
//        def request = new AuthenticationRequest(username: user.username, password: "password")
//        userRepository.findByUsername(user.username) >> Optional.of(user)
//
//        PasswordEncoder encoder = Mock()
//        encoder.matches("password", user.password) >> true
//        authenticationService.metaClass.passwordEncoder = encoder
//
//        when:
//        AuthenticationResponse response = authenticationService.authenticate(request)
//
//        then:
//        response.authenticated
//        response.token == "dummy-token"
//        response.refreshToken == "dummy-token"
//    }
//}
