package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseCreateRequest;
import codeverse.com.web_be.dto.response.ExerciseResponse.ExerciseResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.service.ExerciseService.IExerciseService;
import codeverse.com.web_be.service.LessonService.ILessonService;
import lombok.RequiredArgsConstructor;;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exercise")
@RequiredArgsConstructor
public class ExerciseController {
    private final IExerciseService exerciseService;
    private final ILessonService lessonService;

    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<ExerciseResponse> getExerciseByLessonId(@PathVariable long lessonId) {
        ExerciseResponse response = exerciseService.getExerciseByLessonId(lessonId);
        return ApiResponse.<ExerciseResponse>builder()
                .result(response)
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping
    public ApiResponse<ExerciseResponse> saveExercise(@RequestBody ExerciseCreateRequest request){
        ExerciseResponse response = exerciseService.saveExercise(request);
        return ApiResponse.<ExerciseResponse>builder()
                .result(response)
                .code(HttpStatus.OK.value())
                .build();
    }
}
