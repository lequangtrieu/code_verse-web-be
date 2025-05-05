package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskCreateRequest;
import codeverse.com.web_be.dto.response.ExerciseResponse.ExerciseTaskResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Exercise;
import codeverse.com.web_be.entity.ExerciseTask;
import codeverse.com.web_be.mapper.ExerciseTaskMapper;
import codeverse.com.web_be.service.ExerciseService.IExerciseService;
import codeverse.com.web_be.service.ExerciseTaskService.IExerciseTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercise-task")
@RequiredArgsConstructor
public class ExerciseTaskController {
    private final IExerciseTaskService exerciseTaskService;
    private final IExerciseService exerciseService;
    private final ExerciseTaskMapper exerciseTaskMapper;

    @PostMapping
    public ApiResponse<ExerciseTaskResponse> createExerciseTask(@RequestBody ExerciseTaskCreateRequest request) {
        ExerciseTask exerciseTask = exerciseTaskMapper.exerciseTaskCreateRequestToExerciseTask(request);
        Exercise exercise = exerciseService.findById(request.getExerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
        exerciseTask.setExercise(exercise);
        ExerciseTask createdExerciseTask = exerciseTaskService.save(exerciseTask);
        return ApiResponse.<ExerciseTaskResponse>builder()
                .result(ExerciseTaskResponse.fromEntity(createdExerciseTask))
                .code(HttpStatus.CREATED.value())
                .build();
    }
}
