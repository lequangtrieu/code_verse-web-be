package codeverse.service

import codeverse.com.web_be.dto.request.UserRequest.UserUpdateRequest
import codeverse.com.web_be.dto.response.UserResponse.UserResponse
import codeverse.com.web_be.entity.User
import codeverse.com.web_be.enums.UserRole
import codeverse.com.web_be.exception.AppException
import codeverse.com.web_be.exception.ErrorCode
import codeverse.com.web_be.mapper.UserMapper
import codeverse.com.web_be.repository.*
import codeverse.com.web_be.service.UserService.UserServiceImpl
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification
import spock.lang.Unroll

class UserServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def userMapper = Mock(UserMapper)
    def courseEnrollmentRepository = Mock(CourseEnrollmentRepository)
    def codeSubmissionRepository = Mock(CodeSubmissionRepository)
    def courseRepository = Mock(CourseRepository)
    def lessonProgressRepository = Mock(LessonProgressRepository)
    def lessonRepository = Mock(LessonRepository)

    def service = new UserServiceImpl(
            userRepository, null, userMapper, null, null,
            courseEnrollmentRepository, codeSubmissionRepository, null, null,
            courseRepository, lessonProgressRepository, lessonRepository
    )

    def setup() {
        def auth = Mock(Authentication)
        auth.getName() >> "user@test.com"
        def ctx = Mock(SecurityContext)
        ctx.getAuthentication() >> auth
        SecurityContextHolder.setContext(ctx)
    }

    // ==============================
    //  View Profile
    // ==============================
    def "UTCID01 - ViewProfile success"() {
        given:
        def user = User.builder().id(1L).username("user@test.com")
                .role(UserRole.LEARNER).isVerified(true).isDeleted(false).build()
        userRepository.findByUsername("user@test.com") >> Optional.of(user)
        userMapper.userToUserResponse(user) >> new UserResponse(username: "user@test.com")

        when:
        def result = service.getMyInfo()

        then:
        result.username == "user@test.com"
    }

    def "UTCID02 - ViewProfile fail → Unauthenticated"() {
        given:
        userRepository.findByUsername("user@test.com") >> Optional.empty()

        when:
        service.getMyInfo()

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.USER_NOT_EXISTED
    }

    def "UTCID03 - ViewProfile fail → user not in DB"() {
        given:
        userRepository.findByUsername("user@test.com") >> Optional.empty()

        when:
        service.getMyInfo()

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.USER_NOT_EXISTED
    }

    def "UTCID04 - ViewProfile fail → DB error"() {
        given:
        userRepository.findByUsername("user@test.com") >> { throw new RuntimeException("DB error") }

        when:
        service.getMyInfo()

        then:
        thrown(RuntimeException) // hoặc ErrorCode.UNCATEGORIZED_EXCEPTION nếu bạn wrap
    }

    // ==============================
    //  Update Profile
    // ==============================
    def "UTCID01 - UpdateProfile success"() {
        given:
        def user = User.builder().id(1L).username("user@test.com").build()
        userRepository.findByUsername("user@test.com") >> Optional.of(user)
        userRepository.save(_ as User) >> { args -> args[0] }
        userMapper.userToUserResponse(_ as User) >> new UserResponse(username: "user@test.com", name: "New Name", phoneNumber: "0123456789")

        def req = new UserUpdateRequest(name: "New Name", bio: "Hello", phoneNumber: "0123456789")

        when:
        def result = service.updateMyInfo(req)

        then:
        result.name == "New Name"
        result.phoneNumber == "0123456789"
    }

    def "UTCID02 - UpdateProfile fail → Unauthenticated"() {
        given:
        userRepository.findByUsername("user@test.com") >> Optional.empty()
        def req = new UserUpdateRequest(name: "New Name", bio: "Hello", phoneNumber: "0123456789")

        when:
        service.updateMyInfo(req)

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.USER_NOT_EXISTED
    }

    def "UTCID03 - UpdateProfile fail → user not found in DB"() {
        given:
        userRepository.findByUsername("user@test.com") >> Optional.empty()
        def req = new UserUpdateRequest(name: "New Name", bio: "Hello", phoneNumber: "0123456789")

        when:
        service.updateMyInfo(req)

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.USER_NOT_EXISTED
    }

    def "UTCID04 - UpdateProfile fail → invalid phone"() {
        given:
        def user = User.builder().id(1L).username("user@test.com").build()
        userRepository.findByUsername("user@test.com") >> Optional.of(user)
        def req = new UserUpdateRequest(name: "Valid", bio: "Hello", phoneNumber: "invalid!")

        when:
        service.updateMyInfo(req)

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.INVALID_PHONE
    }

    def "UTCID05 - UpdateProfile fail → empty name"() {
        given:
        def user = User.builder().id(1L).username("user@test.com").build()
        userRepository.findByUsername("user@test.com") >> Optional.of(user)
        def req = new UserUpdateRequest(name: "", bio: "Hello", phoneNumber: "0123456789")

        when:
        service.updateMyInfo(req)

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.INVALID_NAME
    }
}
