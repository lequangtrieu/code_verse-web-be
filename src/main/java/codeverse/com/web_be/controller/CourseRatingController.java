package codeverse.com.web_be.controller;


import codeverse.com.web_be.dto.request.CourseRatingRequest.CourseRatingRequestDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingResponseDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingStatisticsResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.service.AIService.GroqService;
import codeverse.com.web_be.service.CourseRatingService.CourseRatingService;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class CourseRatingController {

    private final CourseRatingService courseRatingService;
    private final FunctionHelper functionHelper;

    @PostMapping("/submit")
    public ResponseEntity<String> submitRating(
            @RequestParam Long userId,
            @RequestBody CourseRatingRequestDto requestDto
    ) {
        if (functionHelper.isOffensive(requestDto.getComment())) {
            return ResponseEntity.badRequest()
                    .body("Your review contains offensive or inappropriate language. Please revise it.");
        }

        courseRatingService.submitRating(userId, requestDto);
        return ResponseEntity.ok("Rating submitted successfully.");
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseRatingResponseDto> getRatingsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseRatingService.getRatingsByCourse(courseId));
    }

    @GetMapping("/course/{courseId}/user/{userId}")
    public ResponseEntity<CourseRatingDto> getUserRatingForCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(courseRatingService.getUserRatingForCourse(courseId, userId));
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<String> updateRating(
            @PathVariable Long ratingId,
            @RequestBody CourseRatingRequestDto requestDto
    ) {
        if (functionHelper.isOffensive(requestDto.getComment())) {
            return ResponseEntity.badRequest()
                    .body("Your review contains offensive or inappropriate language. Please revise it.");
        }

        courseRatingService.updateRating(ratingId, requestDto);
        return ResponseEntity.ok("Rating updated successfully.");
    }

    @GetMapping("/statistics")
    public ApiResponse<List<CourseRatingStatisticsResponse>> getAllRatingStatistics() {
        List<CourseRatingStatisticsResponse> stats = courseRatingService.getAllCourseRatingStatistics();
        return ApiResponse.<List<CourseRatingStatisticsResponse>>builder()
                .result(stats)
                .code(HttpStatus.OK.value())
                .build();
    }


}
