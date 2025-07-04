package codeverse.com.web_be.service.CourseRatingService;

import codeverse.com.web_be.dto.request.CourseRatingRequest.CourseRatingRequestDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingResponseDto;

import java.util.List;

public interface CourseRatingService {
    void submitRating(Long userId, CourseRatingRequestDto requestDto);
    CourseRatingResponseDto getRatingsByCourse(Long courseId);
}