package codeverse.com.web_be.service.LessonService;

import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import codeverse.com.web_be.dto.response.LessonResponse.LessonResponse;
import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.LessonType;
import codeverse.com.web_be.mapper.LessonMapper;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LessonServiceImpl extends GenericServiceImpl<Lesson, Long> implements ILessonService{
    private final ExerciseRepository exerciseRepository;
    private final TheoryRepository theoryRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final LessonMapper lessonMapper;
    private final TestCaseRepository testCaseRepository;
    private final ExerciseTaskRepository exerciseTaskRepository;

    protected LessonServiceImpl(ExerciseRepository exerciseRepository,
                                TheoryRepository theoryRepository,
                                CourseModuleRepository courseModuleRepository,
                                LessonRepository lessonRepository,
                                QuizQuestionRepository quizQuestionRepository,
                                QuizAnswerRepository quizAnswerRepository,
                                LessonMapper lessonMapper, TestCaseRepository testCaseRepository, ExerciseTaskRepository exerciseTaskRepository) {
        super(lessonRepository);
        this.exerciseRepository = exerciseRepository;
        this.theoryRepository = theoryRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lessonRepository = lessonRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.lessonMapper = lessonMapper;
        this.testCaseRepository = testCaseRepository;
        this.exerciseTaskRepository = exerciseTaskRepository;
    }

    @Override
    public LessonResponse createLesson(LessonCreateRequest request) {
        Lesson lesson = lessonMapper.lessonCreateRequestToLesson(request);
        CourseModule courseModule = courseModuleRepository.findById((request.getCourseModuleId()))
                .orElseThrow(() -> new ResourceNotFoundException("Material section not found"));
        lesson.setCourseModule(courseModule);
        Lesson createdLesson = lessonRepository.save(lesson);
        if(createdLesson.getLessonType() == LessonType.CODE){
            Theory theory = new Theory();
            theory.setLesson(createdLesson);
            theory.setTitle("");
            theoryRepository.save(theory);
            Exercise exercise = new Exercise();
            exercise.setLesson(createdLesson);
            exercise.setTitle("");
            exerciseRepository.save(exercise);
        }
        return LessonResponse.fromEntity(createdLesson);
    }

    @Transactional
    @Override
    public LessonResponse updateLesson(Long lessonId, LessonCreateRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        lesson.setTitle(request.getTitle());
        lesson.setDuration(request.getDuration());
        lesson.setExpReward(request.getExpReward());
        lesson.setOrderIndex(request.getOrderIndex());

        LessonType oldType = lesson.getLessonType();
        LessonType newType = request.getLessonType();

        if(oldType != newType){
            switch (oldType) {
                case CODE:
                    testCaseRepository.deleteTestCasesByLessonId(lessonId);
                    exerciseTaskRepository.deleteTasksByLessonId(lessonId);
                    exerciseRepository.deleteByLessonId(lessonId);
                    theoryRepository.deleteByLessonId(lessonId);
                    lesson.setTheory(null);
                    lesson.setExercise(null);
                    break;
                case EXAM:
                    List<QuizQuestion> existingQuestions = quizQuestionRepository.findByLessonId(lessonId);

                    List<Long> questionIds = existingQuestions.stream()
                            .map(QuizQuestion::getId)
                            .toList();

                    quizAnswerRepository.deleteByQuestionIdIn(questionIds);

                    quizQuestionRepository.deleteAllById(questionIds);
                    break;
            }

            if(newType == LessonType.CODE){
                Theory theory = new Theory();
                theory.setLesson(lesson);
                theory.setTitle("");
                theoryRepository.save(theory);
                Exercise exercise = new Exercise();
                exercise.setLesson(lesson);
                exercise.setTitle("");
                exerciseRepository.save(exercise);
            }

            lesson.setLessonType(newType);
        }
        return LessonResponse.fromEntity(lessonRepository.save(lesson));
    }
}
