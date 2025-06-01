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
            for (CourseModuleFullCreateRequest moduleRequest : request.getModules()) {
                CourseModule section = new CourseModule();
                section.setCourse(course);
                section.setTitle(moduleRequest.getTitle());
                section.setOrderIndex(moduleRequest.getOrderIndex());
                courseModuleRepository.save(section);

                if(moduleRequest.getLessons() != null && !moduleRequest.getLessons().isEmpty()) {
                    for (LessonFullCreateRequest lessonRequest : moduleRequest.getLessons()) {
                        Lesson lesson = new Lesson();
                        lesson.setCourseModule(section);
                        lesson.setTitle(lessonRequest.getTitle());
                        lesson.setOrderIndex(lessonRequest.getOrderIndex());
                        lesson.setDuration(lessonRequest.getDuration());
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

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public void updateCourseMaterials(Long courseId, List<CourseModuleUpdateRequest> materials) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Map<Long, CourseModule> existingSections = courseModuleRepository.findByCourseId(courseId).stream()
                .collect(Collectors.toMap(CourseModule::getId, s -> s));

        List<CourseModule> updatedSections = new ArrayList<>();

        if (materials == null || materials.isEmpty()) {
            List<CourseModule> existingMaterials = courseModuleRepository.findByCourseId(courseId);
            for (CourseModule courseModule : existingMaterials) {
                List<Lesson> existingLessons = lessonRepository.findByCourseModuleId(courseModule.getId());
                lessonRepository.deleteAll(existingLessons);
            }
            courseModuleRepository.deleteAll(existingSections.values());
        } else {
            if(materials.stream().allMatch(s -> s.getId() == null)){
                List<CourseModule> existingMaterials = courseModuleRepository.findByCourseId(courseId);
                for (CourseModule courseModule : existingMaterials) {
                    List<Lesson> existingLessons = lessonRepository.findByCourseModuleId(courseModule.getId());
                    lessonRepository.deleteAll(existingLessons);
                }
                courseModuleRepository.deleteAll(existingSections.values());
            }
            for (CourseModuleUpdateRequest sectionReq : materials) {
                CourseModule section;
                boolean isNewSection = (sectionReq.getId() == null || !existingSections.containsKey(sectionReq.getId()));

                if (isNewSection) {
                    section = new CourseModule();
                    courseModuleMapper.updateCourseModuleFromRequest(sectionReq, section);
                    section.setCourse(course);
                    section = courseModuleRepository.save(section);
                } else {
                    section = existingSections.remove(sectionReq.getId());
                    courseModuleMapper.updateCourseModuleFromRequest(sectionReq, section);
                    section.setCourse(course);
                    section = courseModuleRepository.save(section);
                }

                Map<Long, Lesson> existingLessons = (section.getId() != null)
                        ? lessonRepository.findByCourseModuleId(section.getId()).stream()
                        .collect(Collectors.toMap(Lesson::getId, l -> l))
                        : new HashMap<>();

                List<LessonUpdateRequest> lessonReqs = sectionReq.getLessons();
                if (lessonReqs == null || lessonReqs.isEmpty()) {
                    lessonRepository.deleteAll(existingLessons.values());
                } else {
                    List<Lesson> updatedLessons = new ArrayList<>();

                    for (LessonUpdateRequest lessonReq : lessonReqs) {
                        Lesson lesson;
                        boolean isNewLesson = (lessonReq.getId() == null || !existingLessons.containsKey(lessonReq.getId()));

                        if (isNewLesson) {
                            lesson = new Lesson();
                            lessonMapper.updateLessonFromRequest(lessonReq, lesson);
                            lesson.setCourseModule(section);
                            lesson = lessonRepository.save(lesson);
                        } else {
                            lesson = existingLessons.remove(lessonReq.getId());
                            lessonMapper.updateLessonFromRequest(lessonReq, lesson);
                            lesson.setCourseModule(section);
                            lesson = lessonRepository.save(lesson);
                        }

                        // Handle Theory
                        if (lessonReq.getTheory() != null) {
                            if (lesson.getTheory() == null) {
                                Theory theory = theoryMapper.theoryUpdateRequestToTheory(lessonReq.getTheory());
                                theory.setLesson(lesson);
                                theory = theoryRepository.save(theory);
                            } else {
                                theoryMapper.updateTheoryFromRequest(lessonReq.getTheory(), lesson.getTheory());
                            }
                        } else if (lesson.getTheory() != null) {
                            theoryRepository.delete(lesson.getTheory());
                            lesson.setTheory(null);
                        }

                        // Handle Exercise and ExerciseTasks
                        if (lessonReq.getExercise() != null) {
                            Exercise exercise = (lesson.getExercise() == null) ? new Exercise() : lesson.getExercise();
                            exerciseMapper.updateExerciseFromRequest(lessonReq.getExercise(), exercise);
                            exercise.setLesson(lesson);

                            exercise = exerciseRepository.save(exercise);

                            Map<Long, ExerciseTask> existingTasks = (exercise.getTasks() != null)
                                    ? exercise.getTasks().stream()
                                    .filter(t -> t.getId() != null)
                                    .collect(Collectors.toMap(ExerciseTask::getId, t -> t))
                                    : new HashMap<>();

                            List<ExerciseTaskUpdateRequest> taskReqs = lessonReq.getExercise().getTasks();
                            List<ExerciseTask> updatedTasks = new ArrayList<>();

                            if (taskReqs == null || taskReqs.isEmpty()) {
                                if (!existingTasks.isEmpty()) {
                                    exerciseTaskRepository.deleteAll(existingTasks.values());
                                }
                                exercise.setTasks(new ArrayList<>());
                            } else {
                                for (ExerciseTaskUpdateRequest taskReq : taskReqs) {
                                    ExerciseTask task = (taskReq.getId() != null && existingTasks.containsKey(taskReq.getId()))
                                            ? existingTasks.remove(taskReq.getId())
                                            : new ExerciseTask();

                                    exerciseTaskMapper.updateExerciseTaskFromRequest(taskReq, task);
                                    task.setExercise(exercise);
                                    updatedTasks.add(task);
                                }

                                if (!existingTasks.isEmpty()) {
                                    exerciseTaskRepository.deleteAll(existingTasks.values());
                                }

                                exerciseTaskRepository.saveAll(updatedTasks);
                                exercise.setTasks(updatedTasks);
                            }

                        } else if (lesson.getExercise() != null) {
                            if (lesson.getExercise().getTasks() != null) {
                                exerciseTaskRepository.deleteAll(lesson.getExercise().getTasks());
                            }
                            exerciseRepository.delete(lesson.getExercise());
                            lesson.setExercise(null);
                        }

                        updatedLessons.add(lesson);
                    }

                    if (!existingLessons.isEmpty()) {
                        lessonRepository.deleteAll(existingLessons.values());
                    }

                    lessonRepository.saveAll(updatedLessons);
                }

                updatedSections.add(section);
            }

            courseModuleRepository.saveAll(updatedSections);
        }

        if (!existingSections.isEmpty()) {
            courseModuleRepository.deleteAll(existingSections.values());
        }
    }
}