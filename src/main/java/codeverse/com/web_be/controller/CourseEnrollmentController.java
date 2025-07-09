package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.service.CourseEnrollmentService.ICourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
public class CourseEnrollmentController {
    private final ICourseEnrollmentService courseEnrollmentService;

    @GetMapping("/completion")
    public ResponseEntity<ApiResponse<Float>> getCompletionPercentage(
            @RequestParam Long courseId,
            @RequestParam Long userId
    ) {
        Float percent = courseEnrollmentService.getUserCompletionPercentage(userId, courseId);

        ApiResponse<Float> response = ApiResponse.<Float>builder()
                .result(percent)
                .message("Get Completion Percentage Success")
                .code(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
