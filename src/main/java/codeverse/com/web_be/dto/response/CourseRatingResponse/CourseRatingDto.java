package codeverse.com.web_be.dto.response.CourseRatingResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRatingDto {
    private Long id;
    private Long userId;
    private Long courseId;
    private Float rating;
    private String comment;
}
