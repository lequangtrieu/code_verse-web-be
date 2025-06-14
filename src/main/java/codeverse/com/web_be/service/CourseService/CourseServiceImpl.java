package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.dto.request.CodeRequest.CodeRequestDTO;
import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.CourseRequest.CourseUpdateRequest;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleValidationResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseForUpdateResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskFullCreateRequest;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskUpdateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonFullCreateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonUpdateRequest;
import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleFullCreateRequest;
import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleUpdateRequest;
import codeverse.com.web_be.dto.response.CourseResponse.*;
import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.LessonProgressStatus;
import codeverse.com.web_be.enums.LessonType;
import codeverse.com.web_be.mapper.*;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
    private final LessonProgressRepository lessonProgressRepository;
    private final UserRepository userRepository;


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
                             CourseModuleMapper courseModuleMapper,
                             LessonMapper lessonMapper,
                             ExerciseMapper exerciseMapper,
                             ExerciseTaskMapper exerciseTaskMapper,
                             TheoryMapper theoryMapper,
                             CourseEnrollmentRepository courseEnrollmentRepository,
                             LessonProgressRepository lessonProgressRepository, UserRepository userRepository
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
        this.lessonProgressRepository = lessonProgressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Course> findByInstructorId(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<Course> findByInstructorUsername(String username) {
        return courseRepository.findByInstructorUsername(username);
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

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public Course createCourse(CourseCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        User instructor = functionHelper.getActiveUserByUsername(request.getInstructor());

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
    public CourseResponse getCourseById(Long courseId) {
        return courseRepository.selectCourseById(courseId);
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

    @Override
    public List<CourseForUpdateResponse> getAllCoursesByAdmin() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(courseMapper::courseToCourseForUpdateResponse)
                .collect(Collectors.toList());
    }

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

    @Override
    public void updateCourseStatus(Long courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        course.setStatus(request.getStatus());
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
        courseDetailDTO.setLanguage(course.getLanguage());
        return courseDetailDTO;
    }

    @Override
    public String submitCodeHandler(CodeRequestDTO request) {
        Long userId = request.getUserId();
        Long lessonId = request.getLessonId();

        LessonProgress lessonProgress = lessonProgressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    Lesson lesson = lessonRepository.findById(lessonId)
                            .orElseThrow(() -> new RuntimeException("Lesson not found"));
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    return lessonProgressRepository.save(
                            LessonProgress.builder()
                                    .lesson(lesson)
                                    .user(user)
                                    .status(LessonProgressStatus.PASSED)
                                    .startedAt(LocalDateTime.now())
                                    .build()
                    );
                });

        CodeSubmission existingSubmission = lessonProgress.getCodeSubmission();

        if (existingSubmission != null) {
            existingSubmission.setCode(request.getCode());
            existingSubmission.setExecutionTime(request.getExecutionTime());
            existingSubmission.setMemoryUsage(request.getMemoryUsage());
        } else {
            CodeSubmission newSubmission = CodeSubmission.builder()
                    .lessonProgress(lessonProgress)
                    .code(request.getCode())
                    .executionTime(request.getExecutionTime())
                    .memoryUsage(request.getMemoryUsage())
                    .build();

            lessonProgress.setCodeSubmission(newSubmission);
        }

        lessonProgress.setStatus(LessonProgressStatus.PASSED);
        lessonProgressRepository.save(lessonProgress);

        return "submitted";
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
        courseDetailDTO.setLanguage(course.getLanguage());
        return courseDetailDTO;
    }

    @Override
    public String submitCodeHandler(CodeRequestDTO request) {
        Long userId = request.getUserId();
        Long lessonId = request.getLessonId();

        LessonProgress lessonProgress = lessonProgressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    Lesson lesson = lessonRepository.findById(lessonId)
                            .orElseThrow(() -> new RuntimeException("Lesson not found"));
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    return lessonProgressRepository.save(
                            LessonProgress.builder()
                                    .lesson(lesson)
                                    .user(user)
                                    .status(LessonProgressStatus.PASSED)
                                    .startedAt(LocalDateTime.now())
                                    .build()
                    );
                });

        CodeSubmission existingSubmission = lessonProgress.getCodeSubmission();

        if (existingSubmission != null) {
            existingSubmission.setCode(request.getCode());
            existingSubmission.setExecutionTime(request.getExecutionTime());
            existingSubmission.setMemoryUsage(request.getMemoryUsage());
        } else {
            CodeSubmission newSubmission = CodeSubmission.builder()
                    .lessonProgress(lessonProgress)
                    .code(request.getCode())
                    .executionTime(request.getExecutionTime())
                    .memoryUsage(request.getMemoryUsage())
                    .build();

            lessonProgress.setCodeSubmission(newSubmission);
        }

        lessonProgress.setStatus(LessonProgressStatus.PASSED);
        lessonProgressRepository.save(lessonProgress);

        return "submitted";
    }
}