package codeverse.com.web_be.dto.response.LessonProgressDTO;

import codeverse.com.web_be.enums.LessonProgressStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonProgressDTO {
    private Long id;
    private Long userId;
    private Long lessonId;
    private Integer expGained;
    private LessonProgressStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}