package codeverse.com.web_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz_submission_answer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmissionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_question_id", nullable = false)
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_answer_id", nullable = false)
    private QuizAnswer quizAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_submission_id", nullable = false)
    private QuizSubmission quizSubmission;
} 