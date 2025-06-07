package codeverse.com.web_be.service.QuizService;

import codeverse.com.web_be.dto.request.QuizRequest.QuizQuestionCreateRequest;
import codeverse.com.web_be.entity.QuizQuestion;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface IQuizService extends IGenericService<QuizQuestion, Long> {
    void savequizBankByLessonId(Long lessonId, List<QuizQuestionCreateRequest> requests);
}
