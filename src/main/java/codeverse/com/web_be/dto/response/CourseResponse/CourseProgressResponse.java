package codeverse.com.web_be.dto.response.CourseResponse;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String level;
    private String category;
    private String instructor;
    private Double rating;
    private Long ratingCount;
    private Long totalLessons;
    private Long totalDurations;
    private Float completionPercentage;

    public CourseProgressResponse(Long id, String title, String description, String thumbnailUrl,
                                  String level, String category, String instructor,
                                  Long totalLessons, Double rating, Long ratingCount,
                                 Long totalDurations, Float completionPercentage) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.level = level;
        this.category = category;
        this.instructor = instructor;
        this.totalLessons = totalLessons;
        this.rating = rating != null ? rating : 0;
        this.ratingCount = ratingCount != null ? ratingCount : 0;
        this.totalDurations = totalDurations != null ? totalDurations : 0;
        this.completionPercentage = completionPercentage != null ? completionPercentage : 0;
    }
}
