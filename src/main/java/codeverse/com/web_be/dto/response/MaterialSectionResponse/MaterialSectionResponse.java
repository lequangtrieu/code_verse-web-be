package codeverse.com.web_be.dto.response.MaterialSectionResponse;

import codeverse.com.web_be.entity.MaterialSection;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialSectionResponse {
    private Long id;
    private String course;
    private String title;
    private Integer orderIndex;
    private boolean previewable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MaterialSectionResponse fromEntity(MaterialSection materialSection) {
        return MaterialSectionResponse.builder()
                .id(materialSection.getId())
                .course(materialSection.getCourse().getTitle())
                .title(materialSection.getTitle())
                .orderIndex(materialSection.getOrderIndex())
                .previewable(materialSection.isPreviewable())
                .createdAt(materialSection.getCreatedAt())
                .updatedAt(materialSection.getUpdatedAt())
                .build();
    }
}
