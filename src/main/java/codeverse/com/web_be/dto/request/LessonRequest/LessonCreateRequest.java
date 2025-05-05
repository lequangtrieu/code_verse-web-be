package codeverse.com.web_be.dto.request.LessonRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonCreateRequest {
    private Long materialSectionId;
    private String title;
    private Integer orderIndex;
    private String defaultCode;
    private Integer duration;
}
