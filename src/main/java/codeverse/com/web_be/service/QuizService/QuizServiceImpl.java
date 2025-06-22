package codeverse.com.web_be.service.QuizService;

import codeverse.com.web_be.dto.request.QuizRequest.QuizQuestionCreateRequest;
import codeverse.com.web_be.dto.response.QuizResponse.QuizAnswerWithinQuizQuestionResponse;
import codeverse.com.web_be.dto.response.QuizResponse.QuizQuestionWithinLessonResponse;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.QuizAnswer;
import codeverse.com.web_be.entity.QuizQuestion;
import codeverse.com.web_be.mapper.QuizMapper;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.repository.QuizAnswerRepository;
import codeverse.com.web_be.repository.QuizQuestionRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizServiceImpl extends GenericServiceImpl<QuizQuestion, Long> implements IQuizService {
    private final LessonRepository lessonRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizMapper quizMapper;

    public QuizServiceImpl(LessonRepository lessonRepository,
                           QuizQuestionRepository quizQuestionRepository,
                           QuizAnswerRepository quizAnswerRepository,
                           QuizMapper quizMapper) {
        super(quizQuestionRepository);
        this.lessonRepository = lessonRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.quizMapper = quizMapper;
    }

    public void savequizBankByLessonId(Long lessonId, List<QuizQuestionCreateRequest> requests) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        List<QuizQuestion> existingQuestions = quizQuestionRepository.findByLessonId(lessonId);

        List<Long> questionIds = existingQuestions.stream()
                .map(QuizQuestion::getId)
                .toList();

        quizAnswerRepository.deleteByQuestionIdIn(questionIds);

        quizQuestionRepository.deleteAllById(questionIds);

        for(QuizQuestionCreateRequest request : requests) {
            QuizQuestion question = quizMapper.quizQuestionCreateRequestToQuizQuestion(request);
            question.setLesson(lesson);


            List<QuizAnswer> answers = request.getAnswers().stream()
                    .map(answerDto -> {
                        QuizAnswer answer = quizMapper.quizAnswerCreateRequestToQuizAnswer(answerDto);
                        answer.setQuestion(question);
                        return answer;
                    })
                    .toList();

            question.setAnswers(answers);
            quizQuestionRepository.save(question);
        }
    }

    @Override
    public List<QuizQuestionWithinLessonResponse> getQuizBankByLessonId(Long lessonId) {
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        List<QuizQuestion> questions = quizQuestionRepository.findByLessonId(lessonId);
        List<QuizQuestionWithinLessonResponse> responses = questions.stream()
                .map(QuizQuestionWithinLessonResponse::fromEntity)
                .toList();
        for (QuizQuestionWithinLessonResponse response : responses) {
            List<QuizAnswer> answers = quizAnswerRepository.findByQuestionId(response.getId());

            response.setAnswers(answers.stream()
                    .map(QuizAnswerWithinQuizQuestionResponse::fromEntity)
            .toList());
        }
        return responses;
    }
}
