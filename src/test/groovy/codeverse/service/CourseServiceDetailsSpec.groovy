package codeverse.service

import codeverse.com.web_be.dto.response.CourseResponse.AnswersDTO
import codeverse.com.web_be.dto.response.CourseResponse.CourseModuleDTO
import codeverse.com.web_be.dto.response.CourseResponse.ExerciseDTO
import codeverse.com.web_be.dto.response.CourseResponse.LessonDTO
import codeverse.com.web_be.dto.response.CourseResponse.QuestionDTO
import codeverse.com.web_be.dto.response.CourseResponse.TaskDTO
import codeverse.com.web_be.dto.response.CourseResponse.TestCaseDTO
import codeverse.com.web_be.dto.response.CourseResponse.TheoryDTO
import codeverse.com.web_be.entity.CodeSubmission
import codeverse.com.web_be.entity.Course
import codeverse.com.web_be.entity.Lesson
import codeverse.com.web_be.entity.LessonProgress
import codeverse.com.web_be.enums.CodeLanguage
import codeverse.com.web_be.enums.LessonProgressStatus
import codeverse.com.web_be.enums.LessonType
import codeverse.com.web_be.mapper.CourseMapper
import codeverse.com.web_be.repository.CategoryRepository
import codeverse.com.web_be.repository.CourseEnrollmentRepository
import codeverse.com.web_be.repository.CourseModuleRepository
import codeverse.com.web_be.repository.CourseRepository
import codeverse.com.web_be.repository.ExerciseRepository
import codeverse.com.web_be.repository.ExerciseTaskRepository
import codeverse.com.web_be.repository.LessonProgressRepository
import codeverse.com.web_be.repository.LessonRepository
import codeverse.com.web_be.repository.QuizAnswerRepository
import codeverse.com.web_be.repository.QuizQuestionRepository
import codeverse.com.web_be.repository.TestCaseRepository
import codeverse.com.web_be.repository.TheoryRepository
import codeverse.com.web_be.repository.UserRepository
import codeverse.com.web_be.service.AuthenService.AuthenticationService
import codeverse.com.web_be.service.CourseService.CourseServiceImpl
import codeverse.com.web_be.service.EmailService.EmailServiceSender
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper
import codeverse.com.web_be.service.NotificationService.INotificationService
import spock.lang.Specification
import spock.lang.Unroll

class CourseServiceDetailsSpec extends Specification {
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

    def "getCourseDetails → throw if course not found"() {
        given:
        courseRepository.findById(99L) >> Optional.empty()

        when:
        service.getCourseDetails(99L, 1L)

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("Course not found")
    }
}
