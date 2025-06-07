package codeverse.com.web_be.dto.response.CourseModuleResponse;

import codeverse.com.web_be.dto.response.LessonResponse.LessonResponse;
import codeverse.com.web_be.dto.response.LessonResponse.LessonWithinMaterialResponse;
import codeverse.com.web_be.entity.CourseModule;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseModuleResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private List<LessonResponse> lessons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseModuleResponse fromEntity(CourseModule courseModule) {
        return CourseModuleResponse.builder()
                .id(courseModule.getId())
                .title(courseModule.getTitle())
                .orderIndex(courseModule.getOrderIndex())
                .createdAt(courseModule.getCreatedAt())
                .updatedAt(courseModule.getUpdatedAt())
                .build();
    }
}
