package codeverse.com.web_be.service.ExerciseTaskService;

import codeverse.com.web_be.entity.ExerciseTask;
import codeverse.com.web_be.repository.ExerciseTaskRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ExerciseTaskServiceImpl extends GenericServiceImpl<ExerciseTask, Long> implements IExerciseTaskService {
    private ExerciseTaskRepository exerciseTaskRepository;

    protected ExerciseTaskServiceImpl(ExerciseTaskRepository exerciseTaskRepository) {
        super(exerciseTaskRepository);
        this.exerciseTaskRepository = exerciseTaskRepository;
    }
}
