package codeverse.com.web_be.service.ExerciseService;

import codeverse.com.web_be.entity.Exercise;
import codeverse.com.web_be.repository.ExerciseRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ExerciseServiceImpl extends GenericServiceImpl<Exercise, Long> implements IExerciseService{
    private ExerciseRepository exerciseRepository;

    protected ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        super(exerciseRepository);
        this.exerciseRepository = exerciseRepository;
    }
}
