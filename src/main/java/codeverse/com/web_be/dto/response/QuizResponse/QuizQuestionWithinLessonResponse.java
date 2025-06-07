package codeverse.com.web_be.dto.response.QuizResponse;

import codeverse.com.web_be.entity.QuizAnswer;
import codeverse.com.web_be.entity.QuizQuestion;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizQuestionWithinLessonResponse {
    private Long id;
    private String question;
    private String quizType;

    private List<QuizAnswerWithinQuizQuestionResponse> answers;

    public static QuizQuestionWithinLessonResponse fromEntity(QuizQuestion quizQuestion) {
        return QuizQuestionWithinLessonResponse.builder()
                .id(quizQuestion.getId())
                .question(quizQuestion.getQuestion())
                .quizType(String.valueOf(quizQuestion.getQuizType()))
                .build();
    }
}
