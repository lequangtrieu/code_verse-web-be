package codeverse.com.web_be.dto.response.CourseResponse;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String level;
    private String category;
    private String instructor;
    private BigDecimal price;
    private BigDecimal discount;
    private float rating;
    private int ratingCount;
    private int totalStudents;
    private boolean isTrending; // chỗ này có thể viết trong where
    private Long totalLessons;
    private Integer totalDurations;

    public CourseResponse(Long id, String title, String description, String thumbnailUrl, 
                         String level, String category, BigDecimal price, BigDecimal discount, String instructor,
                         Long totalLessons, Float rating, Integer ratingCount, Integer totalStudents, Boolean isTrending, Integer totalDurations) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.level = level;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.instructor = instructor;
        this.totalLessons = totalLessons;
        this.rating = rating != null ? rating : 0;
        this.ratingCount = ratingCount != null ? ratingCount : 0;
        this.totalStudents = totalStudents != null ? totalStudents : 0;
        this.isTrending = isTrending != null && isTrending;
        this.totalDurations = totalDurations != null ? totalDurations : 0;
    }
}
