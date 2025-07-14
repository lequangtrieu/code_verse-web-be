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
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.EmailService.EmailServiceSender;
import codeverse.com.web_be.service.GenericServiceImpl;
import jakarta.mail.MessagingException;
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
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final EmailServiceSender emailService;

    public QuizServiceImpl(LessonRepository lessonRepository,
                           QuizQuestionRepository quizQuestionRepository,
                           QuizAnswerRepository quizAnswerRepository,
                           QuizMapper quizMapper, LessonProgressRepository lessonProgressRepository,
                           CourseEnrollmentRepository courseEnrollmentRepository,
                           EmailServiceSender emailService) {
        super(quizQuestionRepository);
        this.lessonRepository = lessonRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.quizMapper = quizMapper;
        this.lessonProgressRepository = lessonProgressRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.emailService = emailService;

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

            List<QuizAnswer> answers = request.getAnswers().stream()
                    .map(answerRes -> {
                        QuizAnswer answer = quizMapper.quizAnswerCreateRequestToQuizAnswer(answerRes);
                        answer.setQuestion(question);
                        return answer;
                    }).toList();

            question.setAnswers(answers);
            quizQuestionRepository.save(question);
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
    public LessonProgressDTO submitQuiz(Long userId, Long lessonId) throws MessagingException {
        List<LessonProgress> attempts = lessonProgressRepository
                .findAllAttemptsOrderByStartedDesc(userId, lessonId);

        if (attempts.isEmpty()) {
            throw new RuntimeException("No quiz attempt found to submit.");
        }

        LessonProgress lessonProgress = attempts.get(0);
        if (lessonProgress.getStatus() != LessonProgressStatus.PENDING) {
            throw new RuntimeException("This quiz attempt has already been submitted.");
        }

        lessonProgress.setStatus(LessonProgressStatus.FAILED);
        lessonProgress.setExpGained(0);
        lessonProgress.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.saveAndFlush(lessonProgress);
        boolean completed = updateCourseEnrollmentProgress(userId, lessonProgress.getLesson().getId());
        return LessonProgressDTO.builder()
                .id(lessonProgress.getId())
                .userId(lessonProgress.getUser().getId())
                .lessonId(lessonProgress.getLesson().getId())
                .expGained(lessonProgress.getExpGained())
                .status(lessonProgress.getStatus())
                .startedAt(lessonProgress.getStartedAt())
                .completedAt(lessonProgress.getCompletedAt())
                .statusDone(completed)
                .build();
    }

    @Override
    public LessonProgressDTO submitQuizPer(Long userId, Long lessonId, Integer score) throws MessagingException {
        List<LessonProgress> attempts = lessonProgressRepository
                .findAllAttemptsOrderByStartedDesc(userId, lessonId);

        if (attempts.isEmpty()) {
            throw new RuntimeException("No quiz attempt found to submit.");
        }

        LessonProgress lessonProgress = attempts.get(0);

        boolean passed = score >= 80;
        Integer reward = lessonProgress.getLesson().getExpReward();
        if (reward == null || reward < 0) reward = 0;

        lessonProgress.setExpGained(passed ? reward : 0);
        lessonProgress.setStatus(passed ? LessonProgressStatus.PASSED : LessonProgressStatus.FAILED);
        lessonProgress.setCompletedAt(LocalDateTime.now());

        lessonProgressRepository.saveAndFlush(lessonProgress);

        boolean completed = updateCourseEnrollmentProgress(userId, lessonId);

        return LessonProgressDTO.builder()
                .id(lessonProgress.getId())
                .userId(lessonProgress.getUser().getId())
                .lessonId(lessonProgress.getLesson().getId())
                .expGained(lessonProgress.getExpGained())
                .status(lessonProgress.getStatus())
                .startedAt(lessonProgress.getStartedAt())
                .completedAt(lessonProgress.getCompletedAt())
                .statusDone(completed)
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

    private boolean updateCourseEnrollmentProgress(Long userId, Long lessonId) throws MessagingException {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Course course = lesson.getCourseModule().getCourse();

        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByUserIdAndCourseId(userId, course.getId())
                .orElse(null);

        if (enrollment != null) {
            List<Lesson> allLessons = lessonRepository.findByCourseModule_Course_Id(course.getId());
            int totalLessons = allLessons.size();

            List<LessonProgress> passedProgresses = lessonProgressRepository
                    .findByUserIdAndCourseIdAndStatus(userId, course.getId(), LessonProgressStatus.PASSED);

            int passedLessons = passedProgresses.size();
            int totalExp = passedProgresses.stream()
                    .mapToInt(lp -> lp.getExpGained() != null ? lp.getExpGained() : 0)
                    .sum();

            float completionPercentage = totalLessons == 0 ? 0 : (passedLessons * 100f / totalLessons);

            enrollment.setCompletionPercentage(completionPercentage);
            enrollment.setTotalExpGained(totalExp);

            boolean justCompleted = passedLessons == totalLessons && enrollment.getCompletedAt() == null;

            if (justCompleted) {
                enrollment.setCompletedAt(LocalDateTime.now());
                emailService.sendCourseCompletionEmail(enrollment.getUser(), course);
            }

            courseEnrollmentRepository.save(enrollment);
            return justCompleted;
        }

        return false;
    }

}
