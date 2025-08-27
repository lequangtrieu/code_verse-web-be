package codeverse.service

import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest
import codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse
import codeverse.com.web_be.dto.response.CourseResponse.TrainingResponse
import codeverse.com.web_be.entity.Course
import codeverse.com.web_be.entity.CourseEnrollment
import codeverse.com.web_be.entity.Lesson
import codeverse.com.web_be.entity.User
import codeverse.com.web_be.enums.CodeLanguage
import codeverse.com.web_be.enums.CourseLevel
import codeverse.com.web_be.enums.CourseStatus
import codeverse.com.web_be.mapper.CourseMapper
import codeverse.com.web_be.repository.*
import codeverse.com.web_be.service.AuthenService.AuthenticationService
import codeverse.com.web_be.service.CourseService.CourseServiceImpl
import codeverse.com.web_be.service.EmailService.EmailServiceSender
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper
import codeverse.com.web_be.service.NotificationService.INotificationService
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import spock.lang.Specification
import spock.lang.Unroll

class CourseServiceSpec extends Specification {

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

    @Unroll
    def "UTCID01 - success → courses exist in DB"() {
        given:
        def courses = [Course.builder().id(1L).title("Java Basics").status(CourseStatus.PUBLISHED).build()]
        courseRepository.findByInstructorId(99L) >> courses

        when:
        def result = service.findByInstructorId(99L)

        then:
        result.size() == 1
        result[0].title == "Java Basics"
    }

    def "UTCID02 - success → no courses available"() {
        given:
        courseRepository.findByInstructorId(99L) >> []

        when:
        def result = service.findByInstructorId(99L)

        then:
        result.isEmpty()
    }

    def "UTCID03 - success → some courses are DRAFT"() {
        given:
        def courses = [
                Course.builder().id(1L).title("Draft course").status(CourseStatus.DRAFT).build(),
                Course.builder().id(2L).title("Published course").status(CourseStatus.PUBLISHED).build()
        ]
        courseRepository.findByInstructorId(42L) >> courses

        when:
        def result = service.findByInstructorId(42L)

        then:
        result.size() == 2
        result*.status.containsAll([CourseStatus.DRAFT, CourseStatus.PUBLISHED])
    }

    def "UTCID04 - fail → DB error on query"() {
        given:
        courseRepository.findByInstructorId(50L) >> { throw new RuntimeException("DB error") }

        when:
        service.findByInstructorId(50L)

        then:
        def ex = thrown(RuntimeException)
        ex.message == "DB error"
    }

    def "UTCID05 - boundary → course with long values"() {
        given:
        def longTitle = "A" * 500
        def course = Course.builder().id(1L).title(longTitle).status(CourseStatus.PUBLISHED).build()
        courseRepository.findByInstructorId(77L) >> [course]

        when:
        def result = service.findByInstructorId(77L)

        then:
        result.size() == 1
        result[0].title.length() == 500
    }

    def "findTrainingById → success"() {
        given:
        def course = Course.builder().id(10L).title("Spring Boot").level(CourseLevel.BEGINNER).language(CodeLanguage.JAVA).status(CourseStatus.TRAINING_PUBLISHED).build()
        def lesson = Lesson.builder().id(99L).title("Intro").expReward(100).build()

        courseRepository.findById(10L) >> Optional.of(course)
        lessonRepository.findFirstByCourseModule_Course_IdOrderByCourseModule_OrderIndexAscOrderIndexAsc(10L) >> Optional.of(lesson)

        when:
        def result = service.findTrainingById(10L)

        then:
        result instanceof TrainingResponse
        result.courseId == 10L
        result.lessonId == 99L
        result.expReward == 100
    }

    def "findTrainingById → course not found"() {
        given:
        courseRepository.findById(99L) >> Optional.empty()

        when:
        service.findTrainingById(99L)

        then:
        thrown(ResourceNotFoundException)
    }

    def "updateTraining → success"() {
        given:
        def course = Course.builder().id(20L).title("Old").level(CourseLevel.BEGINNER).language(CodeLanguage.JAVA).build()
        def lesson = Lesson.builder().id(30L).expReward(10).build()
        def req = new CourseCreateRequest(title: "NewTitle", level: CourseLevel.ADVANCED, language: CodeLanguage.PYTHON, expReward: 200)

        courseRepository.findById(20L) >> Optional.of(course)
        lessonRepository.findFirstByCourseModule_Course_IdOrderByCourseModule_OrderIndexAscOrderIndexAsc(20L) >> Optional.of(lesson)

        when:
        service.updateTraining(20L, req)

        then:
        1 * courseRepository.save(_)
        1 * lessonRepository.save(_)
        course.title == "NewTitle"
        course.level == CourseLevel.ADVANCED
        lesson.expReward == 200
    }

    def "getCoursesByLearnerId → returns mapped responses"() {
        given:
        def user = User.builder().id(1L).username("learner@test.com").build()
        def course = Course.builder().id(99L).title("Algo").build()
        def enrollment = CourseEnrollment.builder().user(user).course(course).build()
        def response = new CourseResponse(id: 99L, title: "Algo")

        courseEnrollmentRepository.findByUserId(1L) >> [enrollment]
        courseMapper.courseToCourseResponse(course) >> response

        when:
        def result = service.getCoursesByLearnerId(1L)

        then:
        result.size() == 1
        result[0].title == "Algo"
    }

    def "getAllCoursesByLearnerId → delegate to repo"() {
        given:
        def responses = [new CourseProgressResponse()]
        courseRepository.findAllCoursesWithProgressByUserId(1L) >> responses

        expect:
        service.getAllCoursesByLearnerId(1L) == responses
    }

    def "getInProgressCoursesByLearnerId → delegate to repo"() {
        given:
        def responses = [new CourseProgressResponse()]
        courseRepository.findInProgressCourseResponsesByUserId(2L) >> responses

        expect:
        service.getInProgressCoursesByLearnerId(2L) == responses
    }

    def "getCompletedCoursesByLearnerId → delegate to repo"() {
        given:
        def responses = [new CourseProgressResponse()]
        courseRepository.findCompletedCourseResponsesByUserId(3L) >> responses

        expect:
        service.getCompletedCoursesByLearnerId(3L) == responses
    }

    def "getSuggestedCoursesByLearnerId → delegate to repo"() {
        given:
        def responses = [new CourseResponse()]
        courseRepository.findSuggestedCourseResponsesByUserId(4L) >> responses

        expect:
        service.getSuggestedCoursesByLearnerId(4L) == responses
    }
}
