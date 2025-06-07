package codeverse.com.web_be.dto.request.QuizRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizAnswerCreateRequest {
    private String answer;
    private boolean correct;
}
