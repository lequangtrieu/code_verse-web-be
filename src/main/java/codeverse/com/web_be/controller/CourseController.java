package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.mapper.CourseMapper;
import codeverse.com.web_be.service.CourseService.CourseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseServiceImpl courseService;
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

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return courseService.findById(id)
                .map(courseMapper::courseToCourseResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponse> createCourse(@ModelAttribute CourseCreateRequest course) {
        Course  courseCreated = courseService.createFullCourse(course);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseMapper.courseToCourseResponse(courseCreated));
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

}
