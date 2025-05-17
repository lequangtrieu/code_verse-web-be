package codeverse.com.web_be.dto.response.MaterialSectionResponse;

import codeverse.com.web_be.dto.response.LessonResponse.LessonWithinMaterialResponse;
import codeverse.com.web_be.entity.CourseModule;
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
    private Integer orderIndex;
    private boolean previewable = false;

    private List<LessonWithinMaterialResponse> lessons;

    public static MaterialSectionForUpdateResponse fromEntity(CourseModule courseModule) {
        if (courseModule == null) return null;

        return MaterialSectionForUpdateResponse.builder()
                .id(courseModule.getId())
                .title(courseModule.getTitle())
                .orderIndex(courseModule.getOrderIndex())
                .previewable(courseModule.isPreviewable())
                .build();
    }
}
