package codeverse.com.web_be.dto.request.CourseModuleRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseModuleCreateRequest {
    private Long courseId;
    private String title;
    private Integer orderIndex;
}
