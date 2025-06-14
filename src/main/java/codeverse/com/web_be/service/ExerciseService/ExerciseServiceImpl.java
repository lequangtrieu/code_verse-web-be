package codeverse.com.web_be.service.ExerciseService;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseCreateRequest;
import codeverse.com.web_be.dto.response.ExerciseResponse.ExerciseResponse;
import codeverse.com.web_be.entity.Exercise;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.repository.ExerciseRepository;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExerciseServiceImpl extends GenericServiceImpl<Exercise, Long> implements IExerciseService{
    private ExerciseRepository exerciseRepository;
    private LessonRepository lessonRepository;

    protected ExerciseServiceImpl(ExerciseRepository exerciseRepository,
                                  LessonRepository lessonRepository) {
        super(exerciseRepository);
        this.exerciseRepository = exerciseRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public ExerciseResponse getExerciseByLessonId(long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson not found")
        );

        Optional<Exercise> exerciseOptional = exerciseRepository.findByLesson(lesson);
        Exercise exercise = exerciseOptional.orElse(null);
        return ExerciseResponse.fromEntity(exercise, null);
    }

    @Override
    public ExerciseResponse saveExercise(ExerciseCreateRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        Optional<Exercise> oldExercise = exerciseRepository.findByLesson(lesson);
        Exercise exercise = oldExercise.orElse(new Exercise());
        exercise.setLesson(lesson);
        exercise.setTitle(request.getTitle());
        exercise.setInstruction(request.getInstruction());
        return ExerciseResponse.fromEntity(exerciseRepository.save(exercise), null);
    }
}
