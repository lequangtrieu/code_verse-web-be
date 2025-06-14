package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import codeverse.com.web_be.dto.response.LessonResponse.LessonResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.entity.Exercise;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.Theory;
import codeverse.com.web_be.enums.LessonType;
import codeverse.com.web_be.mapper.LessonMapper;
import codeverse.com.web_be.service.ExerciseService.IExerciseService;
import codeverse.com.web_be.service.LessonService.ILessonService;
import codeverse.com.web_be.service.CourseModuleService.ICourseModuleService;
import codeverse.com.web_be.service.TheoryService.ITheoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
public class LessonController {
    private final ILessonService lessonService;

    @PostMapping
    public ApiResponse<LessonResponse> createLesson(@RequestBody LessonCreateRequest request){
        LessonResponse createdLesson = lessonService.createLesson(request);
        return ApiResponse.<LessonResponse>builder()
                .result(createdLesson)
                .code(HttpStatus.CREATED.value())
                .build();
    }

    @PutMapping("/{lessonId}")
    public ApiResponse<LessonResponse> updateLesson(@PathVariable Long lessonId, @RequestBody LessonCreateRequest request){
        LessonResponse updatedLesson = lessonService.updateLesson(lessonId, request);
        return ApiResponse.<LessonResponse>builder()
                .result(updatedLesson)
                .code(HttpStatus.OK.value())
                .build();
    }
}
