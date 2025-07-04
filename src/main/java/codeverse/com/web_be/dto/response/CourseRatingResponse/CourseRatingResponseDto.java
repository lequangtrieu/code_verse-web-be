package codeverse.com.web_be.dto.response.CourseRatingResponse;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class CourseRatingResponseDto {
    private double average;
    private int totalReviews;
    private Map<Integer, Integer> distribution;
    private List<ReviewItem> reviews;
}
