package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskFullCreateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonFullCreateRequest;
import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionFullCreateRequest;
import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.mapper.*;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;

import java.util.List;

@Service
public class CourseServiceImpl extends GenericServiceImpl<Course, Long> implements ICourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final FunctionHelper functionHelper;
    private final MaterialSectionRepository materialSectionRepository;
    private final LessonRepository lessonRepository;
    private final TheoryRepository theoryRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseTaskRepository exerciseTaskRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final CourseMapper courseMapper;
    private final ProgressTrackingRepository progressTrackingRepository;


    public CourseServiceImpl(CourseRepository courseRepository,
                             CategoryRepository categoryRepository,
                             FunctionHelper functionHelper,
                             MaterialSectionRepository materialSectionRepository,
                             LessonRepository lessonRepository,
                             TheoryRepository theoryRepository,
                             ExerciseRepository exerciseRepository,
                             ExerciseTaskRepository exerciseTaskRepository,
                             FirebaseStorageService firebaseStorageService,
                             CourseMapper courseMapper,
                             ProgressTrackingRepository progressTrackingRepository
    ) {
        super(courseRepository);
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.functionHelper = functionHelper;
        this.materialSectionRepository = materialSectionRepository;
        this.lessonRepository = lessonRepository;
        this.theoryRepository = theoryRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseTaskRepository = exerciseTaskRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.courseMapper = courseMapper;
        this.progressTrackingRepository = progressTrackingRepository;
    }

    @Override
    public List<Course> findByInstructorId(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<CourseResponse> getCoursesByLearnerId(Long userId) {
        List<Course> courses = progressTrackingRepository.findByUserId(userId)
                .stream()
                .map(ProgressTracking::getCourse)
                .distinct()
                .toList();

        return courses.stream()
                .map(courseMapper::courseToCourseResponse)
                .toList();
    }

    @Override
    public List<CourseResponse> getInProgressCoursesByLearnerId(Long userId) {
        List<Course> inProgressCourses = progressTrackingRepository.findByUserId(userId)
                .stream()
                .filter(p -> p.getCompletionPercentage() != null && p.getCompletionPercentage() < 100)
                .map(ProgressTracking::getCourse)
                .distinct()
                .toList();

        return inProgressCourses.stream()
                .map(courseMapper::courseToCourseResponse)
                .toList();
    }

    @Override
    public List<CourseResponse> getCompletedCoursesByLearnerId(Long userId) {
        List<Course> completedCourses = progressTrackingRepository.findByUserId(userId)
                .stream()
                .filter(p -> p.getCompletionPercentage() != null && p.getCompletionPercentage() >= 100)
                .map(ProgressTracking::getCourse)
                .distinct()
                .toList();

        return completedCourses.stream()
                .map(courseMapper::courseToCourseResponse)
                .toList();
    }

    @Override
    public List<CourseResponse> getSuggestedCoursesByLearnerId(Long userId) {
        List<Course> allCourses = courseRepository.findAll();

        List<Course> completedOrInProgressCourses = progressTrackingRepository.findByUserId(userId)
                .stream()
                .map(ProgressTracking::getCourse)
                .distinct()
                .toList();

        List<Course> suggestedCourses = allCourses.stream()
                .filter(course -> !completedOrInProgressCourses.contains(course))
                .toList();

        return suggestedCourses.stream()
                .map(courseMapper::courseToCourseResponse)
                .toList();
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.selectAllCourses();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Course createFullCourse(CourseCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        User instructor = functionHelper.getActiveUserByUsername(request.getInstructor());

        String thumbnailUrl = null;
        if(request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            thumbnailUrl = firebaseStorageService.uploadImage(request.getImageFile());
        }

        Course course = courseMapper.courseCreateRequestToCourse(request, category, instructor);
        course.setThumbnailUrl(thumbnailUrl);
        course = courseRepository.save(course);

        if(request.getModules() != null && !request.getModules().isEmpty()) {
            for (MaterialSectionFullCreateRequest moduleRequest : request.getModules()) {
                MaterialSection section = new MaterialSection();
                section.setCourse(course);
                section.setTitle(moduleRequest.getTitle());
                section.setOrderIndex(moduleRequest.getOrderIndex());
                section.setPreviewable(moduleRequest.isPreviewable());
                materialSectionRepository.save(section);

                if(moduleRequest.getLessons() != null && !moduleRequest.getLessons().isEmpty()) {
                    for (LessonFullCreateRequest lessonRequest : moduleRequest.getLessons()) {
                        Lesson lesson = new Lesson();
                        lesson.setMaterialSection(section);
                        lesson.setTitle(lessonRequest.getTitle());
                        lesson.setOrderIndex(lessonRequest.getOrderIndex());
                        lesson.setDuration(lessonRequest.getDuration());
                        lesson.setDefaultCode(lessonRequest.getDefaultCode());
                        lesson = lessonRepository.save(lesson);

                        if (lessonRequest.getTheory() != null) {
                            Theory theory = new Theory();
                            theory.setLesson(lesson);
                            theory.setTitle(lessonRequest.getTheory().getTitle());
                            theory.setContent(lessonRequest.getTheory().getContent());
                            theoryRepository.save(theory);
                        }

                        if (lessonRequest.getExercise() != null) {
                            Exercise exercise = new Exercise();
                            exercise.setLesson(lesson);
                            exercise.setTitle(lessonRequest.getExercise().getTitle());
                            exercise.setExpReward(lessonRequest.getExercise().getExpReward());
                            exercise.setInstruction(lessonRequest.getExercise().getInstruction());
                            exercise = exerciseRepository.save(exercise);

                            if(lessonRequest.getExercise().getTasks() != null && !lessonRequest.getExercise().getTasks().isEmpty()) {
                                for (ExerciseTaskFullCreateRequest taskRequest : lessonRequest.getExercise().getTasks()) {
                                    ExerciseTask task = new ExerciseTask();
                                    task.setExercise(exercise);
                                    task.setDescription(taskRequest.getDescription());
                                    exerciseTaskRepository.save(task);
                                }
                            }

                        }
                    }
                }

            }
        }

        return course;
    }
}