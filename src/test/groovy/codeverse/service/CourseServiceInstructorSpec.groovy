package codeverse.service

import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest
import codeverse.com.web_be.dto.request.CourseRequest.CourseUpdateRequest
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseDetailResponse
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseMoreInfoDTO
import codeverse.com.web_be.dto.response.CourseResponse.CourseForUpdateResponse
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse
import codeverse.com.web_be.dto.response.UserResponse.UserResponse
import codeverse.com.web_be.entity.*
import codeverse.com.web_be.enums.CourseStatus
import codeverse.com.web_be.enums.LessonType
import codeverse.com.web_be.enums.UserRole
import codeverse.com.web_be.mapper.CourseMapper
import codeverse.com.web_be.repository.*
import codeverse.com.web_be.service.AuthenService.AuthenticationService
import codeverse.com.web_be.service.CourseService.CourseServiceImpl
import codeverse.com.web_be.service.EmailService.EmailServiceSender
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper
import codeverse.com.web_be.service.NotificationService.INotificationService
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.ZoneOffset

class CourseServiceInstructorSpec extends Specification {

    // 🔹 Mock all dependencies required by constructor
    def courseRepository = Mock(CourseRepository)
    def categoryRepository = Mock(CategoryRepository)
    def functionHelper = Mock(FunctionHelper)
    def courseModuleRepository = Mock(CourseModuleRepository)
    def lessonRepository = Mock(LessonRepository)
    def theoryRepository = Mock(TheoryRepository)
    def exerciseRepository = Mock(ExerciseRepository)
    def exerciseTaskRepository = Mock(ExerciseTaskRepository)
    def firebaseStorageService = Mock(FirebaseStorageService)
    def courseMapper = Mock(CourseMapper)
    def courseEnrollmentRepository = Mock(CourseEnrollmentRepository)
    def testCaseRepository = Mock(TestCaseRepository)
    def quizQuestionRepository = Mock(QuizQuestionRepository)
    def quizAnswerRepository = Mock(QuizAnswerRepository)
    def lessonProgressRepository = Mock(LessonProgressRepository)
    def userRepository = Mock(UserRepository)
    def authenticationService = Mock(AuthenticationService)
    def emailService = Mock(EmailServiceSender)
    def notificationService = Mock(INotificationService)

    // 🔹 Instantiate service with mocks
    def service = new CourseServiceImpl(
            courseRepository,
            categoryRepository,
            functionHelper,
            courseModuleRepository,
            lessonRepository,
            theoryRepository,
            exerciseRepository,
            exerciseTaskRepository,
            firebaseStorageService,
            courseMapper,
            courseEnrollmentRepository,
            testCaseRepository,
            quizQuestionRepository,
            quizAnswerRepository,
            lessonProgressRepository,
            userRepository,
            authenticationService,
            emailService,
            notificationService
    )

    def setup() {
        // Mock SecurityContext để test findByInstructorUsername / findTrainingByInstructor
        def auth = Mock(Authentication)
        auth.getName() >> "instructorA"
        def context = Mock(SecurityContext)
        context.getAuthentication() >> auth
        SecurityContextHolder.setContext(context)
    }


    def "findByInstructorUsername → filter out TRAINING courses"() {
        given:
        def courses = [
                Course.builder().title("Draft Training").status(CourseStatus.TRAINING_DRAFT).build(),
                Course.builder().title("Normal Course").status(CourseStatus.PUBLISHED).build()
        ]
        courseRepository.findByInstructorUsername("instructorA") >> courses

        when:
        def result = service.findByInstructorUsername("instructorA")

        then:
        result.size() == 1
        result[0].title == "Normal Course"
    }

    def "findTrainingByInstructor → only TRAINING courses"() {
        given:
        def courses = [
                Course.builder().title("Draft Training").status(CourseStatus.TRAINING_DRAFT).build(),
                Course.builder().title("Published Training").status(CourseStatus.TRAINING_PUBLISHED).build(),
                Course.builder().title("Normal Course").status(CourseStatus.PUBLISHED).build()
        ]
        courseRepository.findByInstructorUsername("instructorA") >> courses

        when:
        def result = service.findTrainingByInstructor()

        then:
        result.size() == 2
        result*.title.containsAll(["Draft Training", "Published Training"])
    }

    // --- getPublishedTrainings ---
    @Unroll
    def "getPublishedTrainings → #desc"() {
        given:
        courseRepository.findByStatus(CourseStatus.TRAINING_PUBLISHED) >> input
        input.each { c -> courseMapper.courseToCourseResponse(c) >> new CourseResponse(id: c.id, title: c.title) }

        when:
        def result = service.getPublishedTrainings()

        then:
        result*.title == expected

        where:
        desc                   | input                                                                                 || expected
        "no courses"           | []                                                                                    || []
        "one published course" | [Course.builder().id(1L).title("T1").status(CourseStatus.TRAINING_PUBLISHED).build()] || ["T1"]
        "multiple published"   | [Course.builder().id(2L).title("T2").status(CourseStatus.TRAINING_PUBLISHED).build(),
                                  Course.builder().id(3L).title("T3").status(CourseStatus.TRAINING_PUBLISHED).build()] || ["T2", "T3"]
    }

    // --- createCourse ---
    @Unroll
    def "createCourse → #desc"() {
        given:
        def category = Category.builder().id(100L).name("Programming").build()
        def instructor = User.builder().id(200L).username("instructorA").build()
        def mappedCourse = Course.builder().id(300L).title("Mapped").build()
        def savedCourse = Course.builder().id(301L).title("Saved").thumbnailUrl(expectedUrl).build()

        categoryRepository.findById(100L) >> Optional.of(category)
        functionHelper.getActiveUserByUsername("instructorA") >> instructor
        courseMapper.courseCreateRequestToCourse(_ as CourseCreateRequest, category, instructor) >> mappedCourse
        courseRepository.save(mappedCourse) >> savedCourse

        if (uploadImage) {
            firebaseStorageService.uploadImage(_ as MultipartFile) >> expectedUrl
        }

        when:
        def result = service.createCourse(new CourseCreateRequest(
                categoryId: 100L,
                imageFile: uploadImage ? Mock(MultipartFile) {
                    isEmpty() >> false
                } : null
        ))

        then:
        result.thumbnailUrl == expectedUrl

        where:
        desc                          | uploadImage || expectedUrl
        "no image uploaded"           | false       || null
        "image uploaded successfully" | true        || "http://firebase/image.png"
    }

    def "createCourse → category not found"() {
        given:
        categoryRepository.findById(999L) >> Optional.empty()

        when:
        service.createCourse(new CourseCreateRequest(categoryId: 999L))

        then:
        thrown(IllegalArgumentException)
    }

    // --- getCourseById ---
    def "getCourseById → with multiple modules and lessons"() {
        given:
        def course = Course.builder().id(1L).title("Algo").build()
        def moreInfo = new CourseMoreInfoDTO()
        def module1 = CourseModule.builder().id(10L).title("M1").build()
        def lessons1 = [
                Lesson.builder().id(100L).title("L1").duration(5).build(),
                Lesson.builder().id(101L).title("L2").duration(7).build()
        ]
        def module2 = CourseModule.builder().id(20L).title("M2").build()
        def lessons2 = [
                Lesson.builder().id(200L).title("L3").duration(3).build()
        ]

        courseRepository.findCourseById(1L) >> course
        courseRepository.selectCourseMoreInfoById(1L) >> moreInfo
        courseModuleRepository.findByCourseId(1L) >> [module1, module2]
        lessonRepository.findByCourseModuleIdOrderByOrderIndexAsc(10L) >> lessons1
        lessonRepository.findByCourseModuleIdOrderByOrderIndexAsc(20L) >> lessons2

        when:
        CourseDetailResponse result = service.getCourseById(1L)

        then:
        result.course.title == "Algo"
        result.courseModuleMoreInfoDTOList.size() == 2

        and: "duration sum is correct"
        result.courseModuleMoreInfoDTOList.find { it.courseModule.id == 10L }.totalDuration == 12
        result.courseModuleMoreInfoDTOList.find { it.courseModule.id == 20L }.totalDuration == 3
    }

    def "getCourseById → no modules"() {
        given:
        def course = Course.builder().id(2L).title("EmptyCourse").build()
        def moreInfo = new CourseMoreInfoDTO()
        courseRepository.findCourseById(2L) >> course
        courseRepository.selectCourseMoreInfoById(2L) >> moreInfo
        courseModuleRepository.findByCourseId(2L) >> []

        when:
        def result = service.getCourseById(2L)

        then:
        result.course.title == "EmptyCourse"
        result.courseModuleMoreInfoDTOList.isEmpty()
    }

    // -------------------------
    // updateCourse
    // -------------------------
    @Unroll
    def "updateCourse → handle categoryId = #categoryId, imageFileEmpty = #empty, expect success"() {
        given:
        def course = Course.builder().id(1L).title("Old").build()
        courseRepository.findById(1L) >> Optional.of(course)

        if (categoryId != null) {
            categoryRepository.findById(categoryId) >> Optional.of(Category.builder().id(categoryId).build())
        }

        def request = Mock(CourseUpdateRequest) {
            getCategoryId() >> categoryId
            getImageFile() >> imageFile
            getThumbnailUrl() >> "thumb.png"
        }

        if (!empty) {
            firebaseStorageService.uploadImage(imageFile) >> "uploaded.png"
        }

        courseRepository.save(_) >> { Course c -> c }

        when:
        def result = service.updateCourse(1L, request)

        then:
        result.thumbnailUrl == expectedThumbnail

        where:
        categoryId | imageFile     | empty || expectedThumbnail
        null       | null          | true  || "thumb.png"      // không có category, không upload ảnh
        2L         | null          | true  || "thumb.png"      // có category, không upload ảnh
        3L         | Mock(MultipartFile) { isEmpty() >> false } | false || "uploaded.png"
    }

    def "updateCourse → throw when course not found"() {
        given:
        courseRepository.findById(99L) >> Optional.empty()
        def request = new CourseUpdateRequest()

        when:
        service.updateCourse(99L, request)

        then:
        thrown(ResourceNotFoundException)
    }

    def "updateCourse → throw when category not found"() {
        given:
        def course = Course.builder().id(1L).build()
        courseRepository.findById(1L) >> Optional.of(course)
        categoryRepository.findById(100L) >> Optional.empty()

        def request = new CourseUpdateRequest()
        request.setCategoryId(100L)

        when:
        service.updateCourse(1L, request)

        then:
        thrown(ResourceNotFoundException)
    }

    // -------------------------
    // getLearnersByCourseId
    // -------------------------
    def "getLearnersByCourseId → return sorted learners"() {
        given:
        def user1 = User.builder().id(1L).username("a@a.com").role(UserRole.LEARNER).build()
        def user2 = User.builder().id(2L).username("b@b.com").role(UserRole.LEARNER).build()

        def enroll1 = CourseEnrollment.builder()
                .user(user1)
                .createdAt(LocalDateTime.ofEpochSecond(1000, 0, ZoneOffset.UTC))
                .build()
        def enroll2 = CourseEnrollment.builder()
                .user(user2)
                .createdAt(LocalDateTime.ofEpochSecond(2000, 0, ZoneOffset.UTC))
                .build()

        courseEnrollmentRepository.findByCourseId(1L) >> [enroll1, enroll2]
        authenticationService.getUserByEmail("a@a.com") >> new UserResponse(id: 1L, username: "a@a.com")
        authenticationService.getUserByEmail("b@b.com") >> new UserResponse(id: 2L, username: "b@b.com")

        when:
        def result = service.getLearnersByCourseId(1L)

        then:
        result.size() == 2
        result[0].learner.username == "b@b.com"  // user2 createdAt mới hơn
        result[1].learner.username == "a@a.com"
    }

    def "getLearnersByCourseId → empty enrollments"() {
        given:
        courseEnrollmentRepository.findByCourseId(2L) >> []

        when:
        def result = service.getLearnersByCourseId(2L)

        then:
        result.isEmpty()
    }

    // -------------------------
    // getAllCoursesByAdmin
    // -------------------------
    @Unroll
    def "getAllCoursesByAdmin → filter out TRAINING courses (status = #status)"() {
        given:
        def courses = [Course.builder().id(1L).status(status).build()]
        courseRepository.findAll() >> courses

        if (status == CourseStatus.PUBLISHED) {
            courseMapper.courseToCourseForUpdateResponse(_ as Course) >>
                    CourseForUpdateResponse.builder().id(1L).build()
        }

        when:
        def result = service.getAllCoursesByAdmin()

        then:
        result*.id == expectedIds

        where:
        status                        || expectedIds
        CourseStatus.TRAINING_DRAFT   || []
        CourseStatus.TRAINING_PUBLISHED || []
        CourseStatus.PUBLISHED        || [1L]
    }

    // ---------------------------
    // Tests
    // ---------------------------

    def "validateCourseSection → throw if course not found"() {
        given:
        courseRepository.findById(1L) >> Optional.empty()

        when:
        service.validateCourseSection(1L)

        then:
        thrown(ResourceNotFoundException)
    }

    def "validateCourseSection → error if no modules"() {
        given:
        courseRepository.findById(1L) >> Optional.of(Course.builder().id(1L).build())
        courseModuleRepository.findByCourseId(1L) >> []

        when:
        def result = service.validateCourseSection(1L)

        then:
        !result.valid
        result.errors.contains("Course must have at least one module.")
    }

    def "validateCourseSection → error if module has no lessons"() {
        given:
        def module = CourseModule.builder().id(10L).title("Module1").build()
        courseRepository.findById(1L) >> Optional.of(Course.builder().id(1L).build())
        courseModuleRepository.findByCourseId(1L) >> [module]
        lessonRepository.findByCourseModuleId(10L) >> []

        when:
        def result = service.validateCourseSection(1L)

        then:
        !result.valid
        result.errors[0].contains("must have at least one lesson")
    }

    @Unroll
    def "validateCourseSection CODE → #caseName"() {
        given:
        def module = CourseModule.builder().id(10L).title("M").build()
        def lesson = Lesson.builder().id(20L).title("L").lessonType(LessonType.CODE).build()
        courseRepository.findById(1L) >> Optional.of(Course.builder().id(1L).build())
        courseModuleRepository.findByCourseId(1L) >> [module]
        lessonRepository.findByCourseModuleId(10L) >> [lesson]

        // dùng biến từ where:
        theoryRepository.findByLessonId(20L) >> theoryObj
        exerciseRepository.findByLessonId(20L) >> exerciseObj
        if (exerciseObj != null) {
            exerciseTaskRepository.countByExerciseId(exerciseObj.id) >> taskCount
            testCaseRepository.countByExerciseId(exerciseObj.id) >> testCount
        }

        when:
        def result = service.validateCourseSection(1L)

        then:
        result.valid == expectedValid
        result.errors.any { it.contains(expectedError) } == !expectedValid

        where:
        caseName           | theoryObj                              | exerciseObj                           | taskCount | testCount || expectedValid | expectedError
        "missing theory"   | null                                   | Exercise.builder().id(40L).build()    | 1         | 1         || false         | "missing theory"
        "missing exercise" | Theory.builder().content("x").build()  | null                                  | 0         | 0         || false         | "missing exercise"
        "no tasks"         | Theory.builder().content("x").build()  | Exercise.builder().id(40L).build()    | 0         | 1         || false         | "has no tasks"
        "no test cases"    | Theory.builder().content("x").build()  | Exercise.builder().id(40L).build()    | 1         | 0         || false         | "has no test cases"
        "all valid"        | Theory.builder().content("x").build()  | Exercise.builder().id(40L).build()    | 1         | 1         || true          | ""
    }

    @Unroll
    def "validateCourseSection CODE → #caseName"() {
        given:
        def module = CourseModule.builder().id(10L).title("M").build()
        def lesson = Lesson.builder().id(20L).title("L").lessonType(LessonType.CODE).build()
        courseRepository.findById(1L) >> Optional.of(Course.builder().id(1L).build())
        courseModuleRepository.findByCourseId(1L) >> [module]
        lessonRepository.findByCourseModuleId(10L) >> [lesson]

        // mock theo từng case
        theoryRepository.findByLessonId(20L) >> theoryObj
        exerciseRepository.findByLessonId(20L) >> exerciseObj
        if (exerciseObj != null) {
            exerciseTaskRepository.countByExerciseId(exerciseObj.id) >> taskCount
            testCaseRepository.countByExerciseId(exerciseObj.id) >> testCount
        }

        when:
        def result = service.validateCourseSection(1L)

        then:
        result.valid == expectedValid
        result.errors.any { it.contains(expectedError) } == !expectedValid

        where:
        caseName             | theoryObj                              | exerciseObj                           | taskCount | testCount || expectedValid | expectedError
        "missing theory"     | null                                   | Exercise.builder().id(40L).build()    | 1         | 1         || false         | "missing theory"
        "missing exercise"   | Theory.builder().content("x").build()  | null                                  | 0         | 0         || false         | "missing exercise"
        "no tasks"           | Theory.builder().content("x").build()  | Exercise.builder().id(40L).build()    | 0         | 1         || false         | "has no tasks"
        "no test cases"      | Theory.builder().content("x").build()  | Exercise.builder().id(40L).build()    | 1         | 0         || false         | "has no test cases"
        "all valid"          | Theory.builder().content("x").build()  | Exercise.builder().id(40L).build()    | 1         | 1         || true          | ""
    }

    @Unroll
    def "updateCourseStatus → status = #status triggers correct notifications"() {
        given:
        def instructor = User.builder().id(10L).name("InstructorA").username("ia@x.com").role(UserRole.INSTRUCTOR).build()
        def admin = User.builder().id(99L).name("AdminA").username("admin@x.com").role(UserRole.ADMIN).build()
        def course = Course.builder().id(1L).title("Java 101").status(CourseStatus.DRAFT).instructor(instructor).build()

        courseRepository.findById(1L) >> Optional.of(course)
        userRepository.findAll() >> [admin]

        when:
        def req = new CourseUpdateRequest()
        req.setStatus(status)
        service.updateCourseStatus(1L, req)

        then:
        1 * courseRepository.save(_ as Course)

        // matcher cho từng case
        1 * notificationService.notifyUsers(
                { recipients -> recipientsCheck(recipients, status, instructor, admin) },
                { sender -> senderCheck(sender, status, instructor, admin) },
                _ as String,
                _ as String
        )

        where:
        status << [CourseStatus.PENDING, CourseStatus.PUBLISHED, CourseStatus.DRAFT]
    }

    private boolean recipientsCheck(List<User> recipients, CourseStatus status, User instructor, User admin) {
        switch (status) {
            case CourseStatus.PENDING:    return recipients == [admin]
            case CourseStatus.PUBLISHED:  return recipients == [instructor]
            case CourseStatus.DRAFT:      return recipients == [instructor]
        }
        false
    }

    private boolean senderCheck(User sender, CourseStatus status, User instructor, User admin) {
        switch (status) {
            case CourseStatus.PENDING:    return sender == instructor
            case CourseStatus.PUBLISHED:  return sender == admin
            case CourseStatus.DRAFT:      return sender == admin
        }
        false
    }

    def "updateCourseDiscount → update discount successfully"() {
        given:
        def course = Course.builder().id(2L).title("Spring Boot").discount(BigDecimal.ZERO).build()
        courseRepository.findById(2L) >> Optional.of(course)

        when:
        service.updateCourseDiscount(2L, BigDecimal.valueOf(0.25))

        then:
        course.discount == BigDecimal.valueOf(0.25)
        1 * courseRepository.save(_ as Course)
    }

    def "updateCourseDiscount → throws when course not found"() {
        given:
        courseRepository.findById(99L) >> Optional.empty()

        when:
        service.updateCourseDiscount(99L, BigDecimal.ONE)

        then:
        def ex = thrown(ResourceNotFoundException)
        ex.message == "Course not found"
    }


}
