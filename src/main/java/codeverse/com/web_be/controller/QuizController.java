package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.QuizRequest.QuizQuestionCreateRequest;
import codeverse.com.web_be.dto.response.QuizResponse.QuizQuestionWithinLessonResponse;
import codeverse.com.web_be.dto.response.LessonProgressDTO.LessonProgressDTO;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.LessonProgress;
import codeverse.com.web_be.service.QuizService.IQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<QuizQuestionWithinLessonResponse>> getQuizBankByLessonId(@PathVariable Long lessonId) {
        List<QuizQuestionWithinLessonResponse> responses = quizService.getQuizBankByLessonId(lessonId);
        return ApiResponse.<List<QuizQuestionWithinLessonResponse>>builder()
                .result(responses)
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/progress/{userId}/{lessonId}")
    public ResponseEntity<?> getQuizProgress(@PathVariable Long userId, @PathVariable Long lessonId) {
        try {
            // Gọi phương thức service để lấy tiến trình quiz
            List<LessonProgressDTO> quizProgress = quizService.getQuizProgress(userId, lessonId);

            // Trả về tiến trình nếu tìm thấy
            return ResponseEntity.status(HttpStatus.OK).body(quizProgress);
        } catch (RuntimeException e) {
            // Xử lý trường hợp không tìm thấy tiến trình quiz
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz progress not found");
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get quiz progress: " + e.getMessage());
        }
    }

    @PutMapping("/start/{userId}/{lessonId}")
    public ResponseEntity<ApiResponse> startQuiz(@PathVariable Long userId, @PathVariable Long lessonId) {
        try {
            LessonProgress updatedProgress = quizService.startQuiz(userId, lessonId);

            return ResponseEntity.ok(
                    ApiResponse.<LessonProgress>builder()
                            .code(HttpStatus.OK.value())
                            .message("Quiz started successfully")
                            .result(updatedProgress)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Failed to start quiz")
                            .build());
        }
    }

    @PutMapping("/submit/{userId}/{lessonId}")
    public ResponseEntity<?> submitQuiz(@PathVariable Long userId, @PathVariable Long lessonId) {
        try {
            LessonProgressDTO quizProgressDTO = quizService.submitQuiz(userId, lessonId);
            return ResponseEntity.ok(
                    ApiResponse.<LessonProgressDTO>builder()
                            .code(HttpStatus.OK.value())
                            .message("Quiz submitted successfully")
                            .result(quizProgressDTO)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Failed to submit quiz")
                            .build());
        }
    }

    @PutMapping("/submitPer/{userId}/{lessonId}")
    public ResponseEntity<?> submitQuizPer(@PathVariable Long userId, @PathVariable Long lessonId,@RequestBody Map<String, Integer> payload) {
        try {
            Integer score = payload.get("score");
            LessonProgressDTO quizProgressDTO = quizService.submitQuizPer(userId, lessonId, score);
            return ResponseEntity.ok(
                    ApiResponse.<LessonProgressDTO>builder()
                            .code(HttpStatus.OK.value())
                            .message("Quiz submitted successfully")
                            .result(quizProgressDTO)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Failed to submit quiz")
                            .build());
        }
    }
}
