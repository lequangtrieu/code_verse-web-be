package codeverse.com.web_be.dto.response.CourseModuleResponse;

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
public class CourseModuleForUpdateResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private boolean previewable = false;

    private List<LessonWithinMaterialResponse> lessons;

    public static CourseModuleForUpdateResponse fromEntity(CourseModule courseModule) {
        if (courseModule == null) return null;

        return CourseModuleForUpdateResponse.builder()
                .id(courseModule.getId())
                .title(courseModule.getTitle())
                .orderIndex(courseModule.getOrderIndex())
                .build();
    }
}
