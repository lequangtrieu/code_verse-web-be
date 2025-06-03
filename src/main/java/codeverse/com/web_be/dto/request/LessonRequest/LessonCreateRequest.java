package codeverse.com.web_be.dto.request.LessonRequest;

import codeverse.com.web_be.enums.LessonType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonCreateRequest {
    private Long courseModuleId;
    private String title;
    private Integer orderIndex;
    private Integer duration;
    private LessonType lessonType;
    private Integer expReward;
}
