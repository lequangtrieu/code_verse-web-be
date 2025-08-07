package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.dto.request.CodeRequest.CodeRequestDTO;
import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.CourseRequest.CourseUpdateRequest;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleValidationResponse;
import codeverse.com.web_be.dto.response.CourseResponse.Course.SimpleCourseCardDto;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseDetailResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseModuleMoreInfoDTO;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseMoreInfoDTO;
import codeverse.com.web_be.dto.response.CourseResponse.*;
import codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.LearnerResponse;
import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.*;
import codeverse.com.web_be.mapper.CourseMapper;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.AuthenService.AuthenticationService;
import codeverse.com.web_be.service.EmailService.EmailServiceSender;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.GenericServiceImpl;
import codeverse.com.web_be.service.NotificationService.INotificationService;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends GenericServiceImpl<Course, Long> implements ICourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final FunctionHelper functionHelper;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;
    private final TheoryRepository theoryRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseTaskRepository exerciseTaskRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final CourseMapper courseMapper;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final TestCaseRepository testCaseRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final INotificationService notificationService;
    private final EmailServiceSender emailService;

    public CourseServiceImpl(CourseRepository courseRepository,
                             CategoryRepository categoryRepository,
                             FunctionHelper functionHelper,
                             CourseModuleRepository courseModuleRepository,
                             LessonRepository lessonRepository,
                             TheoryRepository theoryRepository,
                             ExerciseRepository exerciseRepository,
                             ExerciseTaskRepository exerciseTaskRepository,
                             FirebaseStorageService firebaseStorageService,
                             CourseMapper courseMapper,
                             CourseEnrollmentRepository courseEnrollmentRepository,
                             TestCaseRepository testCaseRepository,
                             QuizQuestionRepository quizQuestionRepository,
                             QuizAnswerRepository quizAnswerRepository,
                             LessonProgressRepository lessonProgressRepository,
                             UserRepository userRepository,
                             AuthenticationService authenticationService,
                             EmailServiceSender emailService,
                             INotificationService notificationService
    ) {
        super(courseRepository);
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.functionHelper = functionHelper;
        this.courseModuleRepository = courseModuleRepository;
        this.lessonRepository = lessonRepository;
        this.theoryRepository = theoryRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseTaskRepository = exerciseTaskRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.courseMapper = courseMapper;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.testCaseRepository = testCaseRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    public List<Course> findByInstructorId(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public List<Course> findByInstructorUsername(String username) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        System.out.println("Name:" + name);
        return courseRepository.findByInstructorUsername(name)
                .stream()
                .filter(course -> !course.getStatus().equals(CourseStatus.TRAINING_DRAFT)
                                && !course.getStatus().equals(CourseStatus.TRAINING_PUBLISHED))
                .toList();
    }

    @Override
    public List<Course> findTrainingByInstructor() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        return courseRepository.findByInstructorUsername(name)
                .stream()
                .filter(course -> course.getStatus().equals(CourseStatus.TRAINING_DRAFT)
                        || course.getStatus().equals(CourseStatus.TRAINING_PUBLISHED))
                .toList();
    }

    @Override
    public TrainingResponse findTrainingById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Lesson lesson = lessonRepository.findFirstByCourseModule_Course_IdOrderByCourseModule_OrderIndexAscOrderIndexAsc(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        return TrainingResponse.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .level(String.valueOf(course.getLevel()))
                .language(String.valueOf(course.getLanguage()))
                .status(String.valueOf(course.getStatus()))
                .expReward(lesson.getExpReward())
                .lessonId(lesson.getId())
                .build();
    }

    @Override
    public void updateTraining(Long id, CourseCreateRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        course.setTitle(request.getTitle());
        course.setLevel(request.getLevel());
        course.setLanguage(request.getLanguage());
        courseRepository.save(course);

        Lesson lesson = lessonRepository.findFirstByCourseModule_Course_IdOrderByCourseModule_OrderIndexAscOrderIndexAsc(course.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        lesson.setExpReward(request.getExpReward());
        lessonRepository.save(lesson);
    }

    @Override
    public List<CourseResponse> getCoursesByLearnerId(Long userId) {
        List<Course> courses = courseEnrollmentRepository.findByUserId(userId)
                .stream()
                .map(CourseEnrollment::getCourse)
                .distinct()
                .toList();

        return courses.stream()
                .map(courseMapper::courseToCourseResponse)
                .toList();
    }

    @Override
    public List<CourseProgressResponse> getAllCoursesByLearnerId(Long userId) {
        return courseRepository.findAllCoursesWithProgressByUserId(userId);
    }

    @Override
    public List<CourseProgressResponse> getInProgressCoursesByLearnerId(Long userId) {
        return courseRepository.findInProgressCourseResponsesByUserId(userId);
    }

    @Override
    public List<CourseProgressResponse> getCompletedCoursesByLearnerId(Long userId) {
        return courseRepository.findCompletedCourseResponsesByUserId(userId);
    }


    @Override
    public List<CourseResponse> getSuggestedCoursesByLearnerId(Long userId) {
        return courseRepository.findSuggestedCourseResponsesByUserId(userId);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.selectAllCourses();
    }

    @Override
    public List<CourseResponse> getPublishedTrainings() {
        return courseRepository.findByStatus(CourseStatus.TRAINING_PUBLISHED).stream()
                .map(courseMapper::courseToCourseResponse)
                .toList();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public Course createCourse(CourseCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User instructor = functionHelper.getActiveUserByUsername(name);

        String thumbnailUrl = null;
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            thumbnailUrl = firebaseStorageService.uploadImage(request.getImageFile());
        }

        Course course = courseMapper.courseCreateRequestToCourse(request, category, instructor);
        course.setThumbnailUrl(thumbnailUrl);
        course = courseRepository.save(course);
        return course;
    }

    @Override
    public CourseDetailResponse getCourseById(Long courseId) {
        Course course = courseRepository.findCourseById(courseId);
        CourseMoreInfoDTO courseMoreInfoDTO = courseRepository.selectCourseMoreInfoById(courseId);
        List<CourseModule> courseModules = courseModuleRepository.findByCourseId(courseId);

        List<CourseModuleMoreInfoDTO> courseModuleMoreInfoDTOList = new ArrayList<>();

        for (CourseModule courseModule : courseModules) {
            Integer totalDuration = 0;
            List<Lesson> lessons = lessonRepository.findByCourseModuleIdOrderByOrderIndexAsc(courseModule.getId());

            for (Lesson lesson : lessons) {
                totalDuration += lesson.getDuration();
            }

            CourseModuleMoreInfoDTO courseModuleMoreInfoDTO = new CourseModuleMoreInfoDTO();
            courseModuleMoreInfoDTO.setCourseModule(courseModule);
            courseModuleMoreInfoDTO.setLessons(lessons);
            courseModuleMoreInfoDTO.setTotalDuration(totalDuration);

            courseModuleMoreInfoDTOList.add(courseModuleMoreInfoDTO);
        }


        CourseDetailResponse courseDetailResponse = new CourseDetailResponse();
        courseDetailResponse.setCourse(course);
        courseDetailResponse.setCourseMoreInfo(courseMoreInfoDTO);
        courseDetailResponse.setCourseModuleMoreInfoDTOList(courseModuleMoreInfoDTOList);

        return courseDetailResponse;
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public Course updateCourse(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        courseMapper.courseUpdateRequestToCourse(request, category, course);

        String thumbnailUrl = request.getThumbnailUrl();
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            thumbnailUrl = firebaseStorageService.uploadImage(request.getImageFile());
        }
        course.setThumbnailUrl(thumbnailUrl);
        return courseRepository.save(course);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public List<LearnerResponse> getLearnersByCourseId(Long courseId) {
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourseId(courseId);
        return enrollments.stream().map(enrollment -> {
                    LearnerResponse response = LearnerResponse.fromEntity(enrollment);
                    UserResponse userResponse = authenticationService.getUserByEmail(enrollment.getUser().getUsername());
                    response.setLearner(userResponse);
                    return response;
                })
                .sorted(Comparator.comparing(LearnerResponse::getCompletedAt).reversed())
                .toList();
    }

    @Override
    public List<CourseForUpdateResponse> getAllCoursesByAdmin() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .filter(course -> !course.getStatus().equals(CourseStatus.TRAINING_DRAFT)
                        && !course.getStatus().equals(CourseStatus.TRAINING_PUBLISHED))
                .map(courseMapper::courseToCourseForUpdateResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseModuleValidationResponse validateCourseSection(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<String> errors = new ArrayList<>();

        List<CourseModule> modules = courseModuleRepository.findByCourseId(courseId);
        if (modules.isEmpty()) {
            errors.add("Course must have at least one module.");
        }

        for (CourseModule module : modules) {
            List<Lesson> lessons = lessonRepository.findByCourseModuleId(module.getId());
            if (lessons.isEmpty()) {
                errors.add("Module '" + module.getTitle() + "' must have at least one lesson.");
                continue;
            }

            for (Lesson lesson : lessons) {
                switch (lesson.getLessonType()) {
                    case CODE -> {
                        Theory theory = theoryRepository.findByLessonId(lesson.getId());
                        if (theory == null || theory.getContent() == null) {
                            errors.add("Lesson '" + lesson.getTitle() + "' is missing theory content.");
                        }

                        Exercise exercise = exerciseRepository.findByLessonId(lesson.getId());
                        if (exercise == null) {
                            errors.add("Lesson '" + lesson.getTitle() + "' is missing exercise.");
                            continue;
                        }

                        if (exerciseTaskRepository.countByExerciseId(exercise.getId()) == 0) {
                            errors.add("Exercise in lesson '" + lesson.getTitle() + "' has no tasks.");
                        }

                        if (testCaseRepository.countByExerciseId(exercise.getId()) == 0) {
                            errors.add("Exercise in lesson '" + lesson.getTitle() + "' has no test cases.");
                        }
                    }

                    case EXAM -> {
                        List<QuizQuestion> questions = quizQuestionRepository.findByLessonId(lesson.getId());
                        if (questions.isEmpty()) {
                            errors.add("Lesson '" + lesson.getTitle() + "' has no quiz questions.");
                            continue;
                        }

                        for (QuizQuestion question : questions) {
                            long correctCount = quizAnswerRepository.countByQuestionIdAndIsCorrectTrue(question.getId());
                            if (correctCount == 0) {
                                errors.add("Question '" + question.getQuestion() + "' in lesson '" + lesson.getTitle() + "' has no correct answer.");
                            }
                        }
                    }
                }
            }
        }
        return new CourseModuleValidationResponse(errors.isEmpty(), errors);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Override
    public void updateCourseStatus(Long courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        course.setStatus(request.getStatus());
        courseRepository.save(course);

        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole().equals(UserRole.ADMIN))
                .toList();

        if(request.getStatus().equals(CourseStatus.PENDING)){
            notificationService.notifyUsers(
                    admins,
                    course.getInstructor(),
                    "New Course",
                    "Course " + course.getTitle() + " created by " +
                            course.getInstructor().getName() + " is waiting for approval."
            );
        }
        if(request.getStatus().equals(CourseStatus.PUBLISHED)){
            notificationService.notifyUsers(
                    List.of(course.getInstructor()),
                    admins.get(0),
                    "Your Course has been published!",
                    "Course " + course.getTitle() + " has been published."
            );
        }

    }

    @Override
    public void updateCourseDiscount(Long courseId, BigDecimal discount) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        course.setDiscount(discount);
        courseRepository.save(course);
    }


    @Override
    public CourseDetailDTO getCourseDetails(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        List<CourseModuleDTO> modules = courseRepository.getModulesByCourseId(courseId);
        List<LessonProgress> progresses = courseRepository.findByUserIdAndCourseId(userId, courseId);

        Map<Long, LessonProgress> progressMap = new HashMap<>();
        for (LessonProgress progress : progresses) {
            progressMap.put(progress.getLesson().getId(), progress);
        }

        for (CourseModuleDTO module : modules) {
            List<LessonDTO> lessons = courseRepository.getLessonsByModuleId(module.getId());

            for (LessonDTO lesson : lessons) {
                LessonProgress progress = progressMap.get(lesson.getId());
                if (progress != null) {
                    lesson.setStatus(progress.getStatus());
                    if (progress.getCodeSubmission() != null) {
                        lesson.setCode(progress.getCodeSubmission().getCode());
                    }
                } else {
                    lesson.setStatus(LessonProgressStatus.NOT_STARTED);
                }

                if (Objects.equals(lesson.getLessonType(), LessonType.EXAM)) {
                    List<QuestionDTO> questionDTO = courseRepository.getQuestionByLessonId(lesson.getId());
                    for (QuestionDTO question : questionDTO) {
                        question.setAnswers(courseRepository.getAnswersByQuestionID(question.getId()));
                    }

                    lesson.setQuestions(questionDTO);
                } else {
                    TheoryDTO theory = courseRepository.getTheoryByLessonId(lesson.getId());
                    ExerciseDTO exerciseDTO = courseRepository.getExerciseByLessonId(lesson.getId());

                    if (exerciseDTO != null) {
                        List<TaskDTO> taskDTOS = courseRepository.getExerciseTaskByExerciseID(exerciseDTO.getId());
                        List<TestCaseDTO> testCaseDTO = courseRepository.getTestCaseByExerciseId(exerciseDTO.getId());
                        List<String> tasks = new ArrayList<>();
                        for (TaskDTO taskDTO : taskDTOS) {
                            tasks.add(taskDTO.getDescription());
                        }
                        exerciseDTO.setTasks(tasks);
                        lesson.setTestCases(testCaseDTO);
                        lesson.setExercise(exerciseDTO);
                    } else {
                        lesson.setExercise(null);
                    }

                    lesson.setTheory(theory);
                }


            }
            module.setSubLessons(lessons);
        }
        CourseDetailDTO courseDetailDTO = new CourseDetailDTO();
        courseDetailDTO.setData(modules);
        courseDetailDTO.setLanguage(course.getLanguage() != null ? course.getLanguage() : CodeLanguage.ALL);
        return courseDetailDTO;
    }

    @Override
    public String submitCodeHandler(CodeRequestDTO request) throws MessagingException {
        Long userId = request.getUserId();
        Long lessonId = request.getLessonId();

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        LessonProgress lessonProgress = lessonProgressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElse(null);

        if (lessonProgress == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            lessonProgress = LessonProgress.builder()
                    .lesson(lesson)
                    .user(user)
                    .status(LessonProgressStatus.PASSED)
                    .completedAt(LocalDateTime.now())
                    .expGained(lesson.getExpReward())
                    .build();
        } else {
            if (lessonProgress.getExpGained() == null) {
                lessonProgress.setExpGained(lesson.getExpReward());
            }
            lessonProgress.setLesson(lesson);
        }

        CodeSubmission submission = lessonProgress.getCodeSubmission();
        if (submission != null) {
            submission.setCode(request.getCode());
            submission.setExecutionTime(request.getExecutionTime());
            submission.setMemoryUsage(request.getMemoryUsage());
        } else {
            submission = CodeSubmission.builder()
                    .lessonProgress(lessonProgress)
                    .code(request.getCode())
                    .executionTime(request.getExecutionTime())
                    .memoryUsage(request.getMemoryUsage())
                    .build();

            lessonProgress.setCodeSubmission(submission);
        }

        lessonProgress.setStatus(LessonProgressStatus.PASSED);

        lessonProgressRepository.saveAndFlush(lessonProgress);
        return updateCourseEnrollmentProgress(userId, lesson.getCourseModule().getCourse().getId()) ? "completed": "submitted";
    }

    @Override
    public List<SimpleCourseCardDto> getAuthorCourses(Long instructorId, Long excludedCourseId) {
        return courseRepository.findOtherCoursesByInstructor(instructorId, excludedCourseId);
    }

    @Override
    public List<SimpleCourseCardDto> getPopularCourses() {
        return courseRepository.findPopularCourses(PageRequest.of(0, 5));
    }

    private boolean updateCourseEnrollmentProgress(Long userId, Long courseId) throws MessagingException {
        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow();
                    Course course = courseRepository.findById(courseId).orElseThrow();
                    return CourseEnrollment.builder()
                            .user(user)
                            .course(course)
                            .completionPercentage(0f)
                            .totalExpGained(0)
                            .build();
                });

        List<Lesson> allLessons = lessonRepository.findByCourseModule_Course_Id(courseId);
        int totalLessons = allLessons.size();

        List<LessonProgress> passedProgresses = lessonProgressRepository
                .findByUserIdAndCourseIdAndStatus(userId, courseId, LessonProgressStatus.PASSED);

        int passedCount = passedProgresses.size();
        int totalExp = passedProgresses.stream()
                .mapToInt(p -> p.getExpGained() != null ? p.getExpGained() : 0)
                .sum();

        float percent = totalLessons == 0 ? 0f : (float) passedCount * 100 / totalLessons;

        enrollment.setCompletionPercentage(percent);
        enrollment.setTotalExpGained(totalExp);

        boolean justCompleted = false;
        if (percent == 100f && enrollment.getCompletedAt() == null) {
            enrollment.setCompletedAt(LocalDateTime.now());
            justCompleted = true;

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            emailService.sendCourseCompletionEmail(enrollment.getUser(), course);
        }

        courseEnrollmentRepository.save(enrollment);
        return justCompleted;
    }
}