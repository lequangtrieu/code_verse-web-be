package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import codeverse.com.web_be.dto.response.LessonResponse.LessonResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.mapper.LessonMapper;
import codeverse.com.web_be.service.LessonService.ILessonService;
import codeverse.com.web_be.service.CourseModuleService.ICourseModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
public class LessonController {
    private final LessonMapper lessonMapper;
    private final ILessonService lessonService;
    private final ICourseModuleService materialSectionService;

    @PostMapping
    public ApiResponse<LessonResponse> createLesson(@RequestBody LessonCreateRequest request){
        Lesson lesson = lessonMapper.lessonCreateRequestToLesson(request);
        CourseModule courseModule = materialSectionService.findById((request.getMaterialSectionId()))
                .orElseThrow(() -> new ResourceNotFoundException("Material section not found"));
        lesson.setCourseModule(courseModule);
        Lesson createdLesson = lessonService.save(lesson);
        return ApiResponse.<LessonResponse>builder()
                .result(LessonResponse.fromEntity(createdLesson))
                .code(HttpStatus.CREATED.value())
                .build();
    }
}
