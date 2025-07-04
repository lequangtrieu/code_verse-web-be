package codeverse.com.web_be.dto.request.CourseRatingRequest;

import lombok.Data;

@Data
public class CourseRatingRequestDto {
    private Long courseId;
    private Float rating;
    private String comment;
}
