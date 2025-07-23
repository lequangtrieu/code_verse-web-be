package codeverse.com.web_be.dto.response.CourseRatingResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRatingStatisticsResponse {
    private Long courseId;
    private String courseTitle;
    private Integer year;
    private Double averageRating;
    private Long totalRating;
}
