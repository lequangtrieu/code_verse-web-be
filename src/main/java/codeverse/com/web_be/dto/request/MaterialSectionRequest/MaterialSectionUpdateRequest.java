package codeverse.com.web_be.dto.request.MaterialSectionRequest;

import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonUpdateRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialSectionUpdateRequest {
    private Long id;
    private String title = "";
    private Integer orderIndex;
    private boolean previewable = false;

    private List<LessonUpdateRequest> lessons;
}
