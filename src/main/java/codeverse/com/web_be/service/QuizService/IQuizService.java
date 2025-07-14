package codeverse.com.web_be.service.QuizService;

import codeverse.com.web_be.dto.request.QuizRequest.QuizQuestionCreateRequest;
import codeverse.com.web_be.dto.response.QuizResponse.QuizQuestionWithinLessonResponse;
import codeverse.com.web_be.dto.response.LessonProgressDTO.LessonProgressDTO;
import codeverse.com.web_be.entity.LessonProgress;
import codeverse.com.web_be.entity.QuizQuestion;
import codeverse.com.web_be.service.IGenericService;
import jakarta.mail.MessagingException;

import java.util.List;

public interface IQuizService extends IGenericService<QuizQuestion, Long> {
    void savequizBankByLessonId(Long lessonId, List<QuizQuestionCreateRequest> requests);
    List<QuizQuestionWithinLessonResponse> getQuizBankByLessonId(Long lessonId);
    List<LessonProgressDTO> getQuizProgress(Long userId, Long lessonId );
    LessonProgress startQuiz(Long userId, Long lessonId);
    LessonProgressDTO submitQuiz(Long userId, Long lessonId) throws MessagingException;
    LessonProgressDTO submitQuizPer(Long userId, Long lessonId, Integer score) throws MessagingException;
}
