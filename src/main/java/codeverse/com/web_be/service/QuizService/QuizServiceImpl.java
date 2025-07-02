package codeverse.com.web_be.service.QuizService;

import codeverse.com.web_be.dto.request.QuizRequest.QuizQuestionCreateRequest;
import codeverse.com.web_be.dto.response.LessonProgressDTO.LessonProgressDTO;
import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.LessonProgressStatus;
import codeverse.com.web_be.dto.response.QuizResponse.QuizAnswerWithinQuizQuestionResponse;
import codeverse.com.web_be.dto.response.QuizResponse.QuizQuestionWithinLessonResponse;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.QuizAnswer;
import codeverse.com.web_be.entity.QuizQuestion;
import codeverse.com.web_be.mapper.QuizMapper;
import codeverse.com.web_be.repository.LessonProgressRepository;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.repository.QuizAnswerRepository;
import codeverse.com.web_be.repository.QuizQuestionRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl extends GenericServiceImpl<QuizQuestion, Long> implements IQuizService {
    private final LessonRepository lessonRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizMapper quizMapper;
    private final LessonProgressRepository lessonProgressRepository;

    public QuizServiceImpl(LessonRepository lessonRepository,
                           QuizQuestionRepository quizQuestionRepository,
                           QuizAnswerRepository quizAnswerRepository,
                           QuizMapper quizMapper, LessonProgressRepository lessonProgressRepository) {
        super(quizQuestionRepository);
        this.lessonRepository = lessonRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.quizMapper = quizMapper;
        this.lessonProgressRepository = lessonProgressRepository;
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

        for (QuizQuestionCreateRequest request : requests) {
            QuizQuestion question = quizMapper.quizQuestionCreateRequestToQuizQuestion(request);
            question.setLesson(lesson);
            QuizQuestion savedQuestion = quizQuestionRepository.save(question);

            List<QuizAnswer> answers = request.getAnswers().stream()
                    .map(answerDto -> {
                        QuizAnswer answer = quizMapper.quizAnswerCreateRequestToQuizAnswer(answerDto);
                        answer.setQuestion(savedQuestion);
                        return answer;
                    })
                    .toList();

            quizAnswerRepository.saveAll(answers);
        }
    }

    private LessonProgressDTO toDTO(LessonProgress progress) {
        return LessonProgressDTO.builder()
                .id(progress.getId())
                .userId(progress.getUser().getId())
                .lessonId(progress.getLesson().getId())
                .expGained(progress.getExpGained())
                .status(progress.getStatus())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .build();
    }

    @Override
    public List<LessonProgressDTO> getQuizProgress(Long userId, Long lessonId) {
        List<LessonProgress> progresses = lessonProgressRepository
                .findAllAttemptsOrderByStartedDesc(userId, lessonId);

        progresses.forEach(progress -> {
            if (progress.getStatus() == LessonProgressStatus.PENDING) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime started = progress.getStartedAt();
                Duration elapsed = Duration.between(started, now);

                if (elapsed.toMinutes() >= 30) {
                    progress.setStatus(LessonProgressStatus.FAILED);
                    progress.setCompletedAt(progress.getStartedAt().plusMinutes(30));
                    progress.setExpGained(0);
                    lessonProgressRepository.save(progress);
                }
            }
        });

        return progresses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonProgress startQuiz(Long userId, Long lessonId) {
        List<LessonProgress> progresses = lessonProgressRepository.findAllByUserIdAndLessonId(userId, lessonId);

        boolean hasPassed = progresses.stream()
                .anyMatch(p -> p.getStatus() == LessonProgressStatus.PASSED);

        if (hasPassed) {
            throw new RuntimeException("Quiz already passed. Cannot retry.");
        }

        // Tạo record mới
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        LessonProgress newProgress = LessonProgress.builder()
                .user(new User(userId))
                .lesson(lesson)
                .status(LessonProgressStatus.PENDING)
                .startedAt(LocalDateTime.now())
                .build();

        LessonProgress saved = lessonProgressRepository.save(newProgress);

        return LessonProgress.builder()
                .id(saved.getId())
                .user(saved.getUser())
                .lesson(saved.getLesson())
                .status(saved.getStatus())
                .startedAt(saved.getStartedAt())
                .completedAt(saved.getCompletedAt())
                .expGained(saved.getExpGained())
                .build();
    }

    @Override
    @Transactional
    public LessonProgressDTO submitQuiz(Long userId, Long lessonId) {
        List<LessonProgress> attempts = lessonProgressRepository
                .findAllAttemptsOrderByStartedDesc(userId, lessonId);

        if (attempts.isEmpty()) {
            throw new RuntimeException("No quiz attempt found to submit.");
        }

        LessonProgress lessonProgress = attempts.get(0);

        // Chỉ cho submit nếu đang trong trạng thái PENDING
        if (lessonProgress.getStatus() != LessonProgressStatus.PENDING) {
            throw new RuntimeException("This quiz attempt has already been submitted.");
        }

        lessonProgress.setStatus(LessonProgressStatus.FAILED); // Vì hết giờ
        lessonProgress.setExpGained(0);
        lessonProgress.setCompletedAt(LocalDateTime.now());

        lessonProgressRepository.save(lessonProgress);

        return LessonProgressDTO.builder()
                .id(lessonProgress.getId())
                .userId(lessonProgress.getUser().getId())
                .lessonId(lessonProgress.getLesson().getId())
                .expGained(lessonProgress.getExpGained())
                .status(lessonProgress.getStatus())
                .startedAt(lessonProgress.getStartedAt())
                .completedAt(lessonProgress.getCompletedAt())
                .build();
    }

    @Override
    public LessonProgressDTO submitQuizPer(Long userId, Long lessonId, Integer score) {
        List<LessonProgress> attempts = lessonProgressRepository
                .findAllAttemptsOrderByStartedDesc(userId, lessonId);

        if (attempts.isEmpty()) {
            throw new RuntimeException("No quiz attempt found to submit.");
        }

        LessonProgress lessonProgress = attempts.get(0);

        lessonProgress.setExpGained(score);
        lessonProgress.setStatus(score >= 80 ? LessonProgressStatus.PASSED : LessonProgressStatus.FAILED);
        lessonProgress.setCompletedAt(LocalDateTime.now());

        lessonProgressRepository.save(lessonProgress);

        return LessonProgressDTO.builder()
                .id(lessonProgress.getId())
                .userId(lessonProgress.getUser().getId())
                .lessonId(lessonProgress.getLesson().getId())
                .expGained(lessonProgress.getExpGained())
                .status(lessonProgress.getStatus())
                .startedAt(lessonProgress.getStartedAt())
                .completedAt(lessonProgress.getCompletedAt())
                .build();
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
