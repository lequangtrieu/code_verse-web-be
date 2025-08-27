package codeverse.com.web_be.dto.request.AiCourseSuggestRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AITheoryGenerateRequest {
    private Long lessonId;
    private String theoryTitle;
    private String theoryContent;
}
