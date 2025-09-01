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

//    @Unroll
//    def "getCourseDetails → lessonType=#lessonType with progress #hasProgress and exercise #hasExercise"() {
//        given:
//        // Course exists
//        def course = Course.builder().id(1L).language(CodeLanguage.JAVA).build()
//        courseRepository.findById(1L) >> Optional.of(course)
//
//        // One module
//        def module = new CourseModuleDTO(id: 10L, title: "M1")
//        courseRepository.getModulesByCourseId(1L) >> [module]
//
//        // One lesson
//        def lesson = new LessonDTO(id: 20L, title: "L1", lessonType: lessonType)
//        courseRepository.getLessonsByModuleId(10L) >> [lesson]
//
//        // Progress (optional)
//        if (hasProgress) {
//            def lp = new LessonProgress(
//                    lesson: Lesson.builder().id(20L).build(),
//                    status: LessonProgressStatus.PASSED,
//                    codeSubmission: new CodeSubmission(code: "print('hi')")
//            )
//            courseRepository.findByUserIdAndCourseId(2L, 1L) >> [lp]
//        } else {
//            courseRepository.findByUserIdAndCourseId(2L, 1L) >> []
//        }
//
//        if (lessonType == LessonType.EXAM) {
//            // exam branch
//            def q = new QuestionDTO(id: 30L, question: "Q1")
//            def ans = [new AnswersDTO(id: 100L, answer: "A1", isCorrect: true)]
//            courseRepository.getQuestionByLessonId(20L) >> [q]
//            courseRepository.getAnswersByQuestionID(30L) >> ans
//        } else {
//            // code/theory branch
//            courseRepository.getTheoryByLessonId(20L) >> new TheoryDTO(title: "theory title", content: "theory content")
//
//            if (hasExercise) {
//                def ex = new ExerciseDTO(id: 60L)
//                def tasks = [new TaskDTO(id: 70L, description: "task1")]
//                def tests = [new TestCaseDTO(id: 80L, input: "1", expected: "2")]
//                courseRepository.getExerciseByLessonId(20L) >> ex
//                courseRepository.getExerciseTaskByExerciseID(60L) >> tasks
//                courseRepository.getTestCaseByExerciseId(60L) >> tests
//            } else {
//                courseRepository.getExerciseByLessonId(20L) >> null
//            }
//        }
//
//
//        when:
//        def result = service.getCourseDetails(1L, 2L)
//
//        then:
//        result.data.size() == 1
//        def subLessons = result.data[0].subLessons
//        subLessons.size() == 1
//
//        def l = subLessons[0]
//        if (hasProgress) {
//            assert l.status == LessonProgressStatus.PASSED
//            assert l.code == "print('hi')"
//        } else {
//            assert l.status == LessonProgressStatus.NOT_STARTED
//        }
//
//        if (lessonType == LessonType.EXAM) {
//            assert l.questions*.answers.flatten()*.answer.contains("A1")
//        } else {
//            assert l.theory.content == "theory content"
//            if (hasExercise) {
//                assert l.exercise.tasks.contains("task1")
//                assert l.testCases*.input.contains("1")
//            } else {
//                assert l.exercise == null
//            }
//        }
//
//        where:
//        lessonType         | hasProgress | hasExercise
//        LessonType.EXAM    | true        | false
//        LessonType.EXAM    | false       | false
//        LessonType.CODE    | true        | true
//        LessonType.CODE    | false       | false
//    }
}
