package codeverse.com.web_be.dto.response.CourseResponse;

import codeverse.com.web_be.entity.Course;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String level;
    private String category;
    private String instructor;
    private BigDecimal price;
    private float rating;
    private int totalStudents;
    private boolean isTrending;
    private Long totalLessons;

    public static CourseResponse fromEntity(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .level(course.getLevel().name())
                .category(course.getCategory().getName())
                .price(course.getPrice())
                .build();
    }
}
