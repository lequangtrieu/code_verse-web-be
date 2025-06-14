package codeverse.com.web_be.dto.response.QuizResponse;

import codeverse.com.web_be.entity.QuizAnswer;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizAnswerWithinQuizQuestionResponse {
    private Long id;
    private String answer;
    private Boolean isCorrect;

    public static QuizAnswerWithinQuizQuestionResponse fromEntity(QuizAnswer quizAnswer) {
        return QuizAnswerWithinQuizQuestionResponse.builder()
                .id(quizAnswer.getId())
                .answer(quizAnswer.getAnswer())
                .isCorrect(quizAnswer.isCorrect())
                .build();
    }
}
