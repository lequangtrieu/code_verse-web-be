package codeverse.com.web_be.dto.response.CourseEnrollmentResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CompletedCourseDTO {
    private Long courseId;
    private String courseTitle;
    private String instructorName;
    private Float completionPercentage;
    private LocalDateTime completedAt;
}
