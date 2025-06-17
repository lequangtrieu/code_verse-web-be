package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.CodeRequest.CodeRequestDTO;
import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.CourseRequest.CourseUpdateRequest;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleValidationResponse;
import codeverse.com.web_be.dto.response.CourseResponse.*;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleForUpdateResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseDetailResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.mapper.CourseMapper;
import codeverse.com.web_be.service.CourseService.ICourseService;
import codeverse.com.web_be.service.CourseModuleService.ICourseModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final ICourseService courseService;
    private final ICourseModuleService courseModuleService;
    private final CourseMapper courseMapper;

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

    @GetMapping("/instructor")
    public ApiResponse<List<CourseForUpdateResponse>> getAllCoursesInstructor(@RequestParam String username) {
        return ApiResponse.<List<CourseForUpdateResponse>>builder()
                .result(courseService.findByInstructorUsername(username).stream()
                        .map(courseMapper::courseToCourseForUpdateResponse)
                        .collect(Collectors.toList()))
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
        List<CourseModuleForUpdateResponse> materials = courseModuleService.getCourseModuleListByCourseId(courseId);
        response.setModules(materials);
        return ApiResponse.<CourseForUpdateResponse>builder()
                .result(response)
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
    public ApiResponse<?> submitCodeHandler(@RequestBody CodeRequestDTO request) {
        courseService.submitCodeHandler(request);
        return ApiResponse.<Long>builder()
                .result(request.getLessonId())
                .code(HttpStatus.OK.value())
                .message("Submit code successfully")
                .build();
    }
}
