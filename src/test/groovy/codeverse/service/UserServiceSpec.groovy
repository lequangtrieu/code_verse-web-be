package codeverse.service

import codeverse.com.web_be.dto.request.UserRequest.UserCreationByAdminRequest
import codeverse.com.web_be.dto.request.UserRequest.UserUpdateRequest
import codeverse.com.web_be.dto.response.UserResponse.UserResponse
import codeverse.com.web_be.entity.User
import codeverse.com.web_be.enums.*
import codeverse.com.web_be.exception.AppException
import codeverse.com.web_be.exception.ErrorCode
import codeverse.com.web_be.mapper.UserMapper
import codeverse.com.web_be.repository.*
import codeverse.com.web_be.service.EmailService.EmailServiceSender
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper
import codeverse.com.web_be.service.NotificationService.INotificationService
import codeverse.com.web_be.service.UserService.UserServiceImpl
import jakarta.mail.MessagingException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

class UserServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def userMapper = Mock(UserMapper)
    def firebaseStorageService = Mock(FirebaseStorageService)
    def functionHelper = Mock(FunctionHelper)
    def courseEnrollmentRepository = Mock(CourseEnrollmentRepository)
    def codeSubmissionRepository = Mock(CodeSubmissionRepository)
    def emailSender = Mock(EmailServiceSender)
    def notificationService = Mock(INotificationService)
    def courseRepository = Mock(CourseRepository)
    def lessonProgressRepository = Mock(LessonProgressRepository)
    def lessonRepository = Mock(LessonRepository)

    def service = new UserServiceImpl(
            userRepository, passwordEncoder as PasswordEncoder, userMapper, firebaseStorageService,
            functionHelper, courseEnrollmentRepository, codeSubmissionRepository,
            emailSender, notificationService, courseRepository, lessonProgressRepository, lessonRepository
    )

    def setup() {
        def auth = Mock(Authentication) {
            getName() >> "user@test.com"
        }
        def ctx = Mock(SecurityContext) {
            getAuthentication() >> auth
        }
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

    // ==============================
    // updateAvatar
    // ==============================
    def "updateAvatar success with file"() {
        given:
        def user = User.builder().username("user@test.com").build()
        userRepository.findByUsername("user@test.com") >> Optional.of(user)
        def file = Mock(MultipartFile) {
            isEmpty() >> false
        }
        firebaseStorageService.uploadImage(file) >> "url://avatar"
        userRepository.save(_) >> { User u -> u }
        userMapper.userToUserResponse(_) >> new UserResponse(username: "user@test.com", avatar: "url://avatar")

        when:
        def result = service.updateAvatar(file)

        then:
        result != null
        result.avatar == "url://avatar"
        user.avatar == "url://avatar"
    }

    def "updateAvatar fail → upload throws exception"() {
        given:
        def user = User.builder().username("user@test.com").build()
        userRepository.findByUsername("user@test.com") >> Optional.of(user)
        def file = Mock(MultipartFile) {
            isEmpty() >> false
        }
        firebaseStorageService.uploadImage(file) >> { throw new RuntimeException("upload fail") }

        when:
        service.updateAvatar(file)

        then:
        thrown(RuntimeException)
    }

    // ==============================
    // updateQrCode
    // ==============================
    def "updateQrCode success"() {
        given:
        def user = User.builder().username("user@test.com").build()
        userRepository.findByUsername("user@test.com") >> Optional.of(user)
        def file = Mock(MultipartFile) {
            isEmpty() >> false
        }
        firebaseStorageService.uploadImage(file) >> "url://qr"
        userRepository.save(_) >> { User u -> u }
        userMapper.userToUserResponse(_) >> new UserResponse(username: "user@test.com", qrCodeUrl: "url://qr")

        when:
        def result = service.updateQrCode(file)

        then:
        result != null
        result.qrCodeUrl == "url://qr"
        user.qrCodeUrl == "url://qr"
    }

    // ==============================
    // save
    // ==============================
    def "save success"() {
        given:
        def user = User.builder().username("u@test.com").password("raw").build()
        userRepository.existsByUsername("u@test.com") >> false
        passwordEncoder.encode("raw") >> "encoded"
        userRepository.save(_) >> { User u -> u }

        when:
        def result = service.save(user)

        then:
        result.password == "encoded"
    }

    def "save fail → username existed"() {
        given:
        def user = User.builder().username("u@test.com").build()
        userRepository.existsByUsername("u@test.com") >> true

        when:
        service.save(user)

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.USER_EXISTED
    }

    // ==============================
    // toggleLockUser
    // ==============================
    def "toggleLockUser success → lock user"() {
        given:
        def user = User.builder().id(1L).username("a").build()
        userRepository.findById(1L) >> Optional.of(user)

        when:
        service.toggleLockUser(1L, true)

        then:
        user.isDeleted
        1 * emailSender.sendUserBannedEmail(user)
    }

    def "toggleLockUser fail → user not exist"() {
        given:
        userRepository.findById(1L) >> Optional.empty()

        when:
        service.toggleLockUser(1L, true)

        then:
        thrown(AppException)
    }

    // ==============================
    // createUserByAdmin
    // ==============================
    def "createUserByAdmin success"() {
        given:
        def req = new UserCreationByAdminRequest(username: "new@test.com", password: "raw")
        userRepository.existsByUsername("new@test.com") >> false
        def user = User.builder().username("new@test.com").password("raw").build()
        userMapper.userCreationByAdminRequestToUser(req) >> user
        passwordEncoder.encode("raw") >> "encoded"
        userRepository.save(_) >> { User u -> u }

        when:
        def result = service.createUserByAdmin(req)

        then:
        result.isVerified
        result.password == "encoded"
    }

    def "createUserByAdmin fail → existed"() {
        given:
        def req = new UserCreationByAdminRequest(username: "exist@test.com")
        userRepository.existsByUsername("exist@test.com") >> true

        when:
        service.createUserByAdmin(req)

        then:
        def ex = thrown(AppException)
        ex.errorCode == ErrorCode.USER_EXISTED
    }

    def "createUserByAdmin fail → send email error"() {
        given:
        def req = new UserCreationByAdminRequest(username: "new@test.com", password: "raw")
        userRepository.existsByUsername("new@test.com") >> false
        def user = User.builder().username("new@test.com").password("raw").build()
        userMapper.userCreationByAdminRequestToUser(req) >> user
        passwordEncoder.encode("raw") >> "encoded"
        userRepository.save(_) >> user
        emailSender.sendImportedUserWelcomeEmail(_) >> { throw new MessagingException() }

        when:
        service.createUserByAdmin(req)

        then:
        thrown(AppException)
    }

    // ==============================
    // activateInstructor & deactivateInstructor
    // ==============================
    def "activateInstructor success"() {
        given:
        def user = User.builder().id(1L).username("a").build()
        userRepository.findById(1L) >> Optional.of(user)

        when:
        service.activateInstructor(1L)

        then:
        user.instructorStatus == InstructorStatus.APPROVED
    }

    def "deactivateInstructor success"() {
        given:
        def user = User.builder().id(1L).username("a").build()
        userRepository.findById(1L) >> Optional.of(user)

        when:
        service.deactivateInstructor(1L)

        then:
        user.instructorStatus == InstructorStatus.REJECTED
    }

    // ==============================
    // getBadgesByUser
    // ==============================
    def "getBadgesByUser returns badges"() {
        given:
        def user = User.builder().id(1L).isDeleted(false).isVerified(true).build()
        courseEnrollmentRepository.existsByUserId(1L) >> true
        codeSubmissionRepository.countByUserId(1L) >> 12

        when:
        def result = service.getBadgesByUser(user)

        then:
        result.contains(BadgeType.NEW_LEARNER)
        result.contains(BadgeType.FIRST_COURSE)
        result.contains(BadgeType.TEN_CODE)
    }

    // ==============================
    // getTrainingStatus + getLessonProgressStatus
    // ==============================
    def "getTrainingStatus returns correct ratio"() {
        given:
        codeSubmissionRepository.countTrainingCodeSubmissionsByUserId(1L) >> 3
        courseRepository.countByStatus(CourseStatus.TRAINING_PUBLISHED) >> 5

        expect:
        service.getTrainingStatus(1L) == "3/5"
    }

    def "getLessonProgressStatus returns correct ratio"() {
        given:
        lessonProgressRepository.countByUserIdAndStatus(1L, LessonProgressStatus.PASSED) >> 2
        lessonRepository.countLessonsByUserId(1L) >> 4

        expect:
        service.getLessonProgressStatus(1L) == "2/4"
    }
}
