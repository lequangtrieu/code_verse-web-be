package codeverse.com.web_be.dto.request.CourseModuleRequest;

import codeverse.com.web_be.dto.request.LessonRequest.LessonFullCreateRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseModuleFullCreateRequest {
    private String title = "";
    private Integer orderIndex;
    private boolean previewable = false;

    private List<LessonFullCreateRequest> lessons;
}
