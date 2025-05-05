package codeverse.com.web_be.dto.request.MaterialSectionRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialSectionCreateRequest {
    private Long courseId;
    private String title;
    private Integer orderIndex;
    private boolean previewable = false;
}
