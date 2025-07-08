package codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonthlyLearnerStatisticResponse {
    private Long courseId;
    private String courseTitle;
    private Integer month;
    private Integer year;
    private Long totalEnrolled;
}
