package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskCreateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonFullCreateRequest;
import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionFullCreateRequest;
import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.mapper.*;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends GenericServiceImpl<Course, Long> implements ICourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final MaterialSectionRepository materialSectionRepository;
    private final LessonRepository lessonRepository;
    private final TheoryRepository theoryRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseTaskRepository exerciseTaskRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final CourseMapper courseMapper;


    public CourseServiceImpl(CourseRepository courseRepository,
                             CategoryRepository categoryRepository,
                             UserRepository userRepository,
                             MaterialSectionRepository materialSectionRepository,
                             LessonRepository lessonRepository,
                             TheoryRepository theoryRepository,
                             ExerciseRepository exerciseRepository,
                             ExerciseTaskRepository exerciseTaskRepository,
                             FirebaseStorageService firebaseStorageService,
                             CourseMapper courseMapper
    ) {
        super(courseRepository);
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.materialSectionRepository = materialSectionRepository;
        this.lessonRepository = lessonRepository;
        this.theoryRepository = theoryRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseTaskRepository = exerciseTaskRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.courseMapper = courseMapper;
    }

    @Override
    public List<Course> findByInstructorId(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAllByIsDeletedFalseAndIsPublishedTrue()
                .stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Course createFullCourse(CourseCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        String thumbnailUrl = null;
        if(request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            thumbnailUrl = firebaseStorageService.uploadImage(request.getImageFile());
        }

        Course course = courseMapper.courseCreateRequestToCourse(request, category, instructor);
        course.setThumbnailUrl(thumbnailUrl);
        course = courseRepository.save(course);

        for (MaterialSectionFullCreateRequest moduleRequest : request.getModules()) {
            MaterialSection section = new MaterialSection();
            section.setCourse(course);
            section.setTitle(moduleRequest.getTitle());
            materialSectionRepository.save(section);

            for (LessonFullCreateRequest lessonRequest : moduleRequest.getLessons()) {
                Lesson lesson = new Lesson();
                lesson.setMaterialSection(section);
                lesson.setTitle(lessonRequest.getTitle());
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

                    for (ExerciseTaskCreateRequest taskRequest : lessonRequest.getExercise().getTasks()) {
                        ExerciseTask task = new ExerciseTask();
                        task.setExercise(exercise);
                        task.setDescription(taskRequest.getDescription());
                        exerciseTaskRepository.save(task);
                    }
                }
            }
        }
        return null;
    }
}