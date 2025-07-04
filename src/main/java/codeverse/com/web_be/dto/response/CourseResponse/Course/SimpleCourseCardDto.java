    package codeverse.com.web_be.dto.response.CourseResponse.Course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class SimpleCourseCardDto {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private BigDecimal price;
    private BigDecimal discount;
}