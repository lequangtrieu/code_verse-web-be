package codeverse.com.web_be.dto.request.QuizRequest;

import codeverse.com.web_be.enums.QuizType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizQuestionCreateRequest {
    private String question;
    private QuizType quizType;
    private List<QuizAnswerCreateRequest> answers;
}
