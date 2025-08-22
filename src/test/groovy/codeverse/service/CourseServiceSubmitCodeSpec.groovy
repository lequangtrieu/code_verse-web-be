package codeverse.service

import codeverse.com.web_be.dto.request.CodeRequest.CodeRequestDTO
import codeverse.com.web_be.entity.*
import codeverse.com.web_be.mapper.CourseMapper
import codeverse.com.web_be.repository.*
import codeverse.com.web_be.service.AuthenService.AuthenticationService
import codeverse.com.web_be.service.CourseService.CourseServiceImpl
import codeverse.com.web_be.service.EmailService.EmailServiceSender
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper
import codeverse.com.web_be.service.NotificationService.INotificationService
import spock.lang.Specification

class CourseServiceSubmitCodeSpec extends Specification {

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

    def service = Spy(new CourseServiceImpl(
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
    ))

    def setup() {
        // Quan trọng: luôn trả về Optional, không null
        courseEnrollmentRepository.findByUserIdAndCourseId(_, _) >> Optional.empty()
    }

    def "submitCodeHandler → create new LessonProgress when none exists"() {
        given:
        def course = Course.builder().id(100L).build()
        def module = CourseModule.builder().id(200L).course(course).build()
        def lesson = Lesson.builder().id(300L).courseModule(module).expReward(50).build()
        def user = User.builder().id(10L).username("learner@test.com").build()

        def req = new CodeRequestDTO(userId: 10L, lessonId: 300L, code: "print('hi')", executionTime: 123L, memoryUsage: 456L)

        lessonRepository.findById(300L) >> Optional.of(lesson)
        lessonProgressRepository.findByUserIdAndLessonId(10L, 300L) >> Optional.empty()
        userRepository.findById(10L) >> Optional.of(user)

        service.updateCourseEnrollmentProgress(_, _) >> false

        when:
        def result = service.submitCodeHandler(req)

        then:
        1 * lessonProgressRepository.saveAndFlush(_ as LessonProgress)
        result == "submitted"
    }

    def "submitCodeHandler → update existing LessonProgress without expGained"() {
        given:
        def course = Course.builder().id(1L).build()
        def module = CourseModule.builder().course(course).build()
        def lesson = Lesson.builder().id(2L).courseModule(module).expReward(100).build()

        def progress = LessonProgress.builder()
                .id(99L)
                .lesson(lesson)
                .expGained(null)  // chưa có exp
                .codeSubmission(null)
                .build()

        def req = new CodeRequestDTO(userId: 5L, lessonId: 2L, code: "x=1", executionTime: 11L, memoryUsage: 22L)

        lessonRepository.findById(2L) >> Optional.of(lesson)
        lessonProgressRepository.findByUserIdAndLessonId(5L, 2L) >> Optional.of(progress)

        service.updateCourseEnrollmentProgress(_, _) >> false

        when:
        def result = service.submitCodeHandler(req)

        then:
        progress.expGained == 100
        progress.codeSubmission != null
        result == "submitted"
    }

    def "submitCodeHandler → update existing with codeSubmission already exists"() {
        given:
        def course = Course.builder().id(1L).build()
        def module = CourseModule.builder().course(course).build()
        def lesson = Lesson.builder().id(2L).courseModule(module).expReward(200).build()

        def submission = CodeSubmission.builder().code("old").executionTime(5L).memoryUsage(5L).build()
        def progress = LessonProgress.builder()
                .lesson(lesson)
                .expGained(200)
                .codeSubmission(submission)
                .build()

        def req = new CodeRequestDTO(userId: 6L, lessonId: 2L, code: "new code", executionTime: 99L, memoryUsage: 199L)

        lessonRepository.findById(2L) >> Optional.of(lesson)
        lessonProgressRepository.findByUserIdAndLessonId(6L, 2L) >> Optional.of(progress)

        service.updateCourseEnrollmentProgress(_, _) >> false

        when:
        def result = service.submitCodeHandler(req)

        then:
        submission.code == "new code"
        submission.executionTime == 99L
        submission.memoryUsage == 199L
        result == "submitted"
    }

    def "submitCodeHandler → return 'completed' when updateCourseEnrollmentProgress true"() {
        given:
        def course = Course.builder().id(1L).build()
        def module = CourseModule.builder().course(course).build()
        def lesson = Lesson.builder().id(2L).courseModule(module).expReward(20).build()
        def user = User.builder().id(7L).build()

        def req = new CodeRequestDTO(userId: 7L, lessonId: 2L, code: "done", executionTime: 1L, memoryUsage: 2L)

        lessonRepository.findById(2L) >> Optional.of(lesson)
        lessonProgressRepository.findByUserIdAndLessonId(7L, 2L) >> Optional.empty()
        userRepository.findById(7L) >> Optional.of(user)

        service.updateCourseEnrollmentProgress(_, _) >> true

        // force completed path
        service.updateCourseEnrollmentProgress(_ as Long, _) >> true

        when:
        def result = service.submitCodeHandler(req)

        then:
        result == "completed"
    }
}
