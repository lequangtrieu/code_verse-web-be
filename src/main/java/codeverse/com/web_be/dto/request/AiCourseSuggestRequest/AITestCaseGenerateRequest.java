package codeverse.com.web_be.dto.request.AiCourseSuggestRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AITestCaseGenerateRequest {
    private int testCases;
    private Long lessonId;
}
