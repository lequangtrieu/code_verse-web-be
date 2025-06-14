package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.QuizRequest.QuizQuestionCreateRequest;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.service.QuizService.IQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {
    private final IQuizService quizService;

    @PostMapping("/lesson/{lessonId}")
    public ApiResponse createQuizBank(@PathVariable Long lessonId, @RequestBody List<QuizQuestionCreateRequest> requests) {
        quizService.savequizBankByLessonId(lessonId, requests);
        return ApiResponse.builder()
                .code(HttpStatus.CREATED.value())
                .build();
    }
}
