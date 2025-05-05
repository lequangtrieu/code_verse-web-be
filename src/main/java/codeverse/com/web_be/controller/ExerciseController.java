package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseCreateRequest;
import codeverse.com.web_be.dto.response.ExerciseResponse.ExerciseResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Exercise;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.mapper.ExerciseMapper;
import codeverse.com.web_be.service.ExerciseService.IExerciseService;
import codeverse.com.web_be.service.LessonService.ILessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercise")
@RequiredArgsConstructor
public class ExerciseController {
    private final IExerciseService exerciseService;
    private final ILessonService lessonService;
    private final ExerciseMapper exerciseMapper;

    @PostMapping
    public ApiResponse<ExerciseResponse> createExercise(@RequestBody ExerciseCreateRequest request){
        Exercise exercise = exerciseMapper.exerciseCreateRequestToExercise(request);
        Lesson lesson = lessonService.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        exercise.setLesson(lesson);
        Exercise createdExercise = exerciseService.save(exercise);
        return ApiResponse.<ExerciseResponse>builder()
                .result(ExerciseResponse.fromEntity(createdExercise))
                .code(HttpStatus.CREATED.value())
                .build();
    }
}
