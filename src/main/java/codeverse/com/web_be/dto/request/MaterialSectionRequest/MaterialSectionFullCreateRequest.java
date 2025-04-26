package codeverse.com.web_be.dto.request.MaterialSectionRequest;

import codeverse.com.web_be.dto.request.LessonRequest.LessonFullCreateRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialSectionFullCreateRequest {
    private String title;

    private List<LessonFullCreateRequest> lessons;
}
