package codeverse.com.web_be.service.CourseRatingService;

import codeverse.com.web_be.dto.request.CourseRatingRequest.CourseRatingRequestDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingResponseDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingStatisticsResponse;

import java.util.List;

public interface CourseRatingService {
    CourseRatingDto getUserRatingForCourse(Long courseId, Long userId);
    void updateRating(Long ratingId, CourseRatingRequestDto requestDto);
    void submitRating(Long userId, CourseRatingRequestDto requestDto);
    CourseRatingResponseDto getRatingsByCourse(Long courseId);
    List<CourseRatingStatisticsResponse> getAllCourseRatingStatistics();
}