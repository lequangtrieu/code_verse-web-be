package codeverse.com.web_be.service.ExerciseService;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseCreateRequest;
import codeverse.com.web_be.dto.response.ExerciseResponse.ExerciseResponse;
import codeverse.com.web_be.entity.Exercise;
import codeverse.com.web_be.service.IGenericService;

public interface IExerciseService extends IGenericService<Exercise, Long> {
    ExerciseResponse getExerciseByLessonId(long lessonId);
    ExerciseResponse saveExercise(ExerciseCreateRequest request);
}
