package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.response.CourseResponse.CourseForUpdateResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionForUpdateResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.mapper.CourseMapper;
import codeverse.com.web_be.service.CourseService.ICourseService;
import codeverse.com.web_be.service.MaterialSectionService.IMaterialSectionService;
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
    private final IMaterialSectionService materialSectionService;
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

    @GetMapping("/admin")
    public ApiResponse<List<CourseForUpdateResponse>> getAllCoursesAdmin() {
        return ApiResponse.<List<CourseForUpdateResponse>>builder()
                .result(courseService.findAll().stream()
                        .map(courseMapper::courseToCourseForUpdateResponse)
                        .collect(Collectors.toList()))
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getCourseById(@PathVariable Long id) {
        return ApiResponse.<CourseResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get Course Success")
                .result(courseService.getCourseById(id))
                .build();
    }

    @GetMapping("/admin/{courseId}")
    public ApiResponse<CourseForUpdateResponse> getFullCourseById(@PathVariable Long courseId) {
        Course course = courseService.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        CourseForUpdateResponse response = courseMapper.courseToCourseForUpdateResponse(course);
        List<MaterialSectionForUpdateResponse> materials = materialSectionService.getMaterialSectionListByCourseId(courseId);
        response.setModules(materials);
        return ApiResponse.<CourseForUpdateResponse>builder()
                .result(response)
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> createCourse(@ModelAttribute CourseCreateRequest course) {
        Course  courseCreated = courseService.createFullCourse(course);

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

    @GetMapping("/user/{userId}/in-progress")
    public ResponseEntity<List<CourseResponse>> getInProgressCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(courseService.getInProgressCoursesByLearnerId(userId));
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<CourseResponse>> getCompletedCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(courseService.getCompletedCoursesByLearnerId(userId));
    }

    @GetMapping("/user/{userId}/suggested")
    public ResponseEntity<List<CourseResponse>> getSuggestedCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(courseService.getSuggestedCoursesByLearnerId(userId));
    }

}
