package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.TheoryRequest.TheoryCreateRequest;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.dto.response.TheoryResponse.TheoryResponse;
import codeverse.com.web_be.entity.Theory;
import codeverse.com.web_be.service.TheoryService.ITheoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/theory")
@RequiredArgsConstructor
public class TheoryController {
    private final ITheoryService theoryService;

    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<TheoryResponse> getheoryByLessonId(@PathVariable Long lessonId) {
        TheoryResponse response = theoryService.getTheoryByLessonId(lessonId);
        return ApiResponse.<TheoryResponse>builder()
                .result(response)
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<TheoryResponse> saveTheory(@ModelAttribute TheoryCreateRequest request){
        TheoryResponse response = theoryService.saveTheory((request));
        return ApiResponse.<TheoryResponse>builder()
                .result(response)
                .code(HttpStatus.OK.value())
                .build();
    }
}
