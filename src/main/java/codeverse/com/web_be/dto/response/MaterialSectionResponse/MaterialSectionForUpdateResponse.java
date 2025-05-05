package codeverse.com.web_be.dto.response.MaterialSectionResponse;

import codeverse.com.web_be.dto.response.LessonResponse.LessonWithinMaterialResponse;
import codeverse.com.web_be.entity.MaterialSection;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialSectionForUpdateResponse {
    private Long id;
    private String title;

    private List<LessonWithinMaterialResponse> lessons;

    public static MaterialSectionForUpdateResponse fromEntity(MaterialSection materialSection) {
        if (materialSection == null) return null;

        return MaterialSectionForUpdateResponse.builder()
                .id(materialSection.getId())
                .title(materialSection.getTitle())
                .build();
    }
}
