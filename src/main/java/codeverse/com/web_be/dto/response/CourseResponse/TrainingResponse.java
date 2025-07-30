package codeverse.com.web_be.dto.response.CourseResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingResponse {
    private Long courseId;
    private String title;
    private String level;
    private String language;
    private String status;
    private Integer expReward;
    private Long lessonId;
}
