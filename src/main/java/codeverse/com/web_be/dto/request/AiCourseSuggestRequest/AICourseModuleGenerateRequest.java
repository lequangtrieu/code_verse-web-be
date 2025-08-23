package codeverse.com.web_be.dto.request.AiCourseSuggestRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AICourseModuleGenerateRequest {
    private Long courseId;
    private int modules;
    private int lessons;
}
