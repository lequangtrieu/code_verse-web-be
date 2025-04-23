package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.service.CourseService.CourseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseServiceImpl courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        return ResponseEntity.ok(
                ApiResponse.<List<CourseResponse>>builder()
                        .result(courseService.getAllCourses())
                        .message("Success")
                        .build()
        );
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
//        return ResponseEntity.ok(
//                ApiResponse.<CourseResponse>builder()
//                        .result(courseService.getById(id))
//                        .message("Success")
//                        .build()
//        );
//    }

}
