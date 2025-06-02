package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.CourseRequest.CourseUpdateRequest;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskFullCreateRequest;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskUpdateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonFullCreateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonUpdateRequest;
import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleFullCreateRequest;
import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleUpdateRequest;
import codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse;
import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.mapper.*;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;

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
    private final CourseModuleMapper courseModuleMapper;
    private final LessonMapper lessonMapper;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseTaskMapper exerciseTaskMapper;
    private final TheoryMapper theoryMapper;
    private final CourseEnrollmentRepository courseEnrollmentRepository;


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
                             CourseEnrollmentRepository courseEnrollmentRepository
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
        this.courseModuleMapper = courseModuleMapper;
        this.lessonMapper = lessonMapper;
        this.exerciseMapper = exerciseMapper;
        this.exerciseTaskMapper = exerciseTaskMapper;
        this.theoryMapper = theoryMapper;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
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
        if(request.getImageFile() != null && !request.getImageFile().isEmpty()) {
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
        if(request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        courseMapper.courseUpdateRequestToCourse(request, category, course);

        String thumbnailUrl = request.getThumbnailUrl();
        if(request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            thumbnailUrl = firebaseStorageService.uploadImage(request.getImageFile());
        }
        course.setThumbnailUrl(thumbnailUrl);
        return courseRepository.save(course);
    }
}