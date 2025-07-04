package codeverse.com.web_be.controller;


import codeverse.com.web_be.dto.request.CourseRatingRequest.CourseRatingRequestDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingResponseDto;
import codeverse.com.web_be.service.CourseRatingService.CourseRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class CourseRatingController {

    private final CourseRatingService courseRatingService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitRating(
            @RequestParam Long userId,
            @RequestBody CourseRatingRequestDto requestDto
    ) {
        courseRatingService.submitRating(userId, requestDto);
        return ResponseEntity.ok("Rating submitted successfully.");
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseRatingResponseDto> getRatingsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseRatingService.getRatingsByCourse(courseId));
    }

}
