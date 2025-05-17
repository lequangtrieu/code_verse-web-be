package codeverse.com.web_be.dto.response.CourseModuleResponse;

import codeverse.com.web_be.entity.CourseModule;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseModuleResponse {
    private Long id;
    private String course;
    private String title;
    private Integer orderIndex;
    private boolean previewable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseModuleResponse fromEntity(CourseModule courseModule) {
        return CourseModuleResponse.builder()
                .id(courseModule.getId())
                .course(courseModule.getCourse().getTitle())
                .title(courseModule.getTitle())
                .orderIndex(courseModule.getOrderIndex())
                .previewable(courseModule.isPreviewable())
                .createdAt(courseModule.getCreatedAt())
                .updatedAt(courseModule.getUpdatedAt())
                .build();
    }
}
