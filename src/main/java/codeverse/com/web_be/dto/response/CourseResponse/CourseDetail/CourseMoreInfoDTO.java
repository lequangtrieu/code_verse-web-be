package codeverse.com.web_be.dto.response.CourseResponse.CourseDetail;

import lombok.Data;

@Data
public class CourseMoreInfoDTO {
    private Double rating;
    private Long ratingCount;
    private Long totalStudents;
    private Boolean isTrending;
    private Long totalLessons;
    private Long totalDurations;
    private String category;
    private String instructor;
    private Long instructorId;

    public CourseMoreInfoDTO() {
    }

    public CourseMoreInfoDTO(
            Double rating,
            Long ratingCount,
            Long totalStudents,
            Boolean isTrending,
            Long totalLessons,
            Long totalDurations,
            String category,
            String instructor,
            Long instructorId
    ) {
        this.rating = rating != null ? rating : 0;
        this.ratingCount = ratingCount != null ? ratingCount : 0;
        this.totalStudents = totalStudents != null ? totalStudents : 0;
        this.isTrending = isTrending != null && isTrending;
        this.totalLessons = totalLessons != null ? totalLessons : 0;
        this.totalDurations = totalDurations != null ? totalDurations : 0;
        this.category = category;
        this.instructor = instructor;
        this.instructorId = instructorId;
    }
}
