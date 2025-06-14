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
    private Double rating;
    private Long ratingCount;
    private Long totalStudents;
    private Boolean isTrending;
    private Long totalLessons;
    private Long totalDurations;

    public CourseResponse(Long id, String title, String description, String thumbnailUrl, 
                         String level, String category, BigDecimal price, BigDecimal discount, String instructor,
                         Long totalLessons, Double rating, Long ratingCount, Long totalStudents, Boolean isTrending, Long totalDurations) {
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
        this.isTrending = isTrending != null ? isTrending : false;
        this.totalDurations = totalDurations != null ? totalDurations : 0;
    }
}
