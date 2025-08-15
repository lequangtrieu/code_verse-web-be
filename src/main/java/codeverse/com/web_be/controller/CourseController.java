package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.CodeRequest.CodeRequestDTO;
import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.CourseRequest.CourseUpdateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import codeverse.com.web_be.dto.response.CourseEnrollmentResponse.CourseEnrollmentStatusDTO;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleValidationResponse;
import codeverse.com.web_be.dto.response.CourseResponse.*;
import codeverse.com.web_be.dto.response.CourseResponse.Course.SimpleCourseCardDto;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseDetailResponse;
import codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.LearnerResponse;
import codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.MonthlyLearnerStatisticResponse;
import codeverse.com.web_be.dto.response.LessonResponse.LessonResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.CourseEnrollment;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.enums.LessonType;
import codeverse.com.web_be.mapper.CourseMapper;
import codeverse.com.web_be.service.CourseEnrollmentService.ICourseEnrollmentService;
import codeverse.com.web_be.repository.CourseEnrollmentRepository;
import codeverse.com.web_be.service.CourseModuleService.ICourseModuleService;
import codeverse.com.web_be.service.CourseService.ICourseService;
import codeverse.com.web_be.service.LessonService.ILessonService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final ICourseService courseService;
    private final ICourseEnrollmentService courseEnrollmentService;
    private final CourseMapper courseMapper;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final ICourseModuleService courseModuleService;
    private final ILessonService lessonService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        return ResponseEntity.ok(
                ApiResponse.<List<CourseResponse>>builder()
                        .result(courseService.getAllCourses())
                        .message("GetAllCourses Success")
                        .code(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/training/published")
    public ResponseEntity<List<CourseResponse>> getAllPublishedTrainings() {
        List<CourseResponse> trainings = courseService.getPublishedTrainings();
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/instructor")
    public ApiResponse<List<CourseForUpdateResponse>> getAllCoursesInstructor(@RequestParam String username) {
        return ApiResponse.<List<CourseForUpdateResponse>>builder()
                .result(courseService.findByInstructorUsername(username).stream()
                        .map(courseMapper::courseToCourseForUpdateResponse)
                        .toList())
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/training/instructor")
    public ApiResponse<List<CourseForUpdateResponse>> getAllTrainingsInstructor() {
        return ApiResponse.<List<CourseForUpdateResponse>>builder()
                .result(courseService.findTrainingByInstructor().stream()
                        .map(courseMapper::courseToCourseForUpdateResponse)
                        .toList())
                .code(HttpStatus.OK.value())
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> updateCourse(@PathVariable Long id, @ModelAttribute CourseUpdateRequest request) {
        Course updatedCourse = courseService.updateCourse(id, request);
        return ApiResponse.<CourseResponse>builder()
                .result(courseMapper.courseToCourseResponse(updatedCourse))
                .code(HttpStatus.OK.value())
                .build();
    }

    @PutMapping(value = "training/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> updateTraining(@PathVariable Long id, @ModelAttribute CourseCreateRequest request) {
        courseService.updateTraining(id, request);
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseDetailResponse> getCourseById(@PathVariable Long id) {
        return ApiResponse.<CourseDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get Course Success")
                .result(courseService.getCourseById(id))
                .build();
    }

    @GetMapping("/{courseId}/for-instructor")
    public ApiResponse<CourseForUpdateResponse> getFullCourseById(@PathVariable Long courseId) {
        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        CourseForUpdateResponse response = courseMapper.courseToCourseForUpdateResponse(course);
        return ApiResponse.<CourseForUpdateResponse>builder()
                .result(response)
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/training/{id}/for-instructor")
    public ApiResponse<TrainingResponse> getTrainingById(@PathVariable Long id) {
        TrainingResponse response = courseService.findTrainingById(id);
        return ApiResponse.<TrainingResponse>builder()
                .result(response)
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{courseId}/learners")
    public ApiResponse<List<LearnerResponse>> getLearnersByCourse(@PathVariable Long courseId) {
        List<LearnerResponse> responses = courseService.getLearnersByCourseId(courseId);
        return ApiResponse.<List<LearnerResponse>>builder()
                .result(responses)
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/monthly-stats/instructor")
    public ApiResponse<List<MonthlyLearnerStatisticResponse>> getMonthlyStatistics(@RequestParam String username){
        List<MonthlyLearnerStatisticResponse> responses = courseEnrollmentService.getMonthlyStats(username);
        return ApiResponse.<List<MonthlyLearnerStatisticResponse>>builder()
                .result(responses)
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> createCourse(@ModelAttribute CourseCreateRequest course) {
        Course  courseCreated = courseService.createCourse(course);

        return ApiResponse.<CourseResponse>builder()
                .result(courseMapper.courseToCourseResponse(courseCreated))
                .code(HttpStatus.CREATED.value())
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/training")
    public ApiResponse<?> createTraining(@ModelAttribute CourseCreateRequest course) {
        Course courseCreated = courseService.createCourse(course);
        CourseModule module = CourseModule.builder()
                .course(courseCreated)
                .title("Training Module")
                .orderIndex(1)
                .build();
        module = courseModuleService.save(module);

        LessonResponse lesson = lessonService.createLesson(LessonCreateRequest.builder()
                .courseModuleId(module.getId())
                .title("Training Lesson")
                .orderIndex(1)
                .lessonType(LessonType.CODE)
                .duration(10)
                .expReward(course.getExpReward())
                .build());
        return ApiResponse.builder()
                .result(Map.of("lessonId", lesson.getId(),
                        "courseId", courseCreated.getId()))
                .code(HttpStatus.CREATED.value())
                .build();
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByLearner(@PathVariable Long userId) {
        List<CourseResponse> courses = courseService.getCoursesByLearnerId(userId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/user/{userId}/all-courses")
    public ResponseEntity<List<CourseProgressResponse>> getAllCoursesByLearnerId(@PathVariable Long userId) {
        return ResponseEntity.ok(courseService.getAllCoursesByLearnerId(userId));
    }

    @GetMapping("/user/{userId}/in-progress")
    public ResponseEntity<List<CourseProgressResponse>> getInProgressCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(courseService.getInProgressCoursesByLearnerId(userId));
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<CourseProgressResponse>> getCompletedCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(courseService.getCompletedCoursesByLearnerId(userId));
    }

    @GetMapping("/user/{userId}/suggested")
    public ResponseEntity<List<CourseResponse>> getSuggestedCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(courseService.getSuggestedCoursesByLearnerId(userId));
    }

    @GetMapping("/admin/instructor/{id}")
    public ApiResponse<List<CourseForUpdateResponse>> getAllCoursesInstructorById(@PathVariable Long id) {
        return ApiResponse.<List<CourseForUpdateResponse>>builder()
                .result(courseService.findByInstructorId(id).stream()
                        .map(courseMapper::courseToCourseForUpdateResponse)
                        .collect(Collectors.toList()))
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<CourseForUpdateResponse>> getAllCoursesForAdmin() {
        List<CourseForUpdateResponse> courses = courseService.getAllCoursesByAdmin();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/validate")
    public ApiResponse<CourseModuleValidationResponse> validateCourse(@PathVariable Long courseId) {
        CourseModuleValidationResponse response = courseService.validateCourseSection(courseId);
        return ApiResponse.<CourseModuleValidationResponse>builder()
                .result(response)
                .code(HttpStatus.OK.value())
                .build();
    }

    @PatchMapping("/{courseId}/status")
    public ApiResponse updateCourseStatus(@PathVariable Long courseId, @RequestBody CourseUpdateRequest request) {
        courseService.updateCourseStatus(courseId, request);
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .build();
    }

    @PatchMapping("/{courseId}/discount/{discount}")
    public ApiResponse updateCourseDiscount(@PathVariable Long courseId, @PathVariable BigDecimal discount) {
        courseService.updateCourseDiscount(courseId, discount);
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{userId}/{courseId}/lesson")
    public ApiResponse<CourseDetailDTO> getCourseDetails(@PathVariable Long courseId, @PathVariable Long userId) {
        CourseDetailDTO courseDetail = courseService.getCourseDetails(courseId, userId);
        return ApiResponse.<CourseDetailDTO>builder()
                .result(courseDetail)
                .code(HttpStatus.OK.value())
                .message("Course details fetched successfully")
                .build();
    }

    @PostMapping("/submitCode")
    public ApiResponse<?> submitCodeHandler(@RequestBody CodeRequestDTO request) throws MessagingException {
        String message = courseService.submitCodeHandler(request);
        return ApiResponse.<Long>builder()
                .result(request.getLessonId())
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @GetMapping("/{courseId}/enrollment-status")
    public ResponseEntity<CourseEnrollmentStatusDTO> getEnrollmentStatus(
            @PathVariable Long courseId,
            @RequestParam Long userId) {

        Optional<CourseEnrollment> enrollmentOpt = courseEnrollmentRepository
                .findByUserIdAndCourseId(userId, courseId);

        if (enrollmentOpt.isPresent()) {
            CourseEnrollment enrollment = enrollmentOpt.get();
            return ResponseEntity.ok(
                    new CourseEnrollmentStatusDTO(true, enrollment.getCompletionPercentage() != null ? enrollment.getCompletionPercentage() : 0f)
            );
        } else {
            return ResponseEntity.ok(new CourseEnrollmentStatusDTO(false, 0f));
        }
    }

    @GetMapping("/authorOther/{instructorId}")
    public ResponseEntity<List<SimpleCourseCardDto>> getAuthorCourses(
            @PathVariable Long instructorId,
            @RequestParam Long excludeCourseId
    ) {
        List<SimpleCourseCardDto> result = courseService.getAuthorCourses(instructorId, excludeCourseId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<SimpleCourseCardDto>> getPopularCourses() {
        List<SimpleCourseCardDto> result = courseService.getPopularCourses();
        return ResponseEntity.ok(result);
    }
}
