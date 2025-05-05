package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.TheoryRequest.TheoryCreateRequest;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.dto.response.TheoryResponse.TheoryResponse;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.Theory;
import codeverse.com.web_be.mapper.TheoryMapper;
import codeverse.com.web_be.service.LessonService.ILessonService;
import codeverse.com.web_be.service.TheoryService.ITheoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/theory")
@RequiredArgsConstructor
public class TheoryController {
    private final ITheoryService theoryService;
    private final ILessonService lessonService;
    private final TheoryMapper theoryMapper;

    @PostMapping
    public ApiResponse<TheoryResponse> createTheory(@RequestBody TheoryCreateRequest request){
        Theory theory = theoryMapper.TheoryCreateRequestToTheory(request);
        Lesson lesson = lessonService.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        theory.setLesson(lesson);
        Theory createdTheory = theoryService.save(theory);
        return ApiResponse.<TheoryResponse>builder()
                .result(TheoryResponse.fromEntity(createdTheory))
                .code(HttpStatus.CREATED.value())
                .build();
    }
}
