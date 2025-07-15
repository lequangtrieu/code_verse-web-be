package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.WithdrawalRequest.WithdrawalRequestCreateRequest;
import codeverse.com.web_be.dto.response.WithdrawalResponse.WithdrawalRequestDTO;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.service.WithdrawalRequestService.WithdrawalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors/{instructorId}/withdrawals")
@RequiredArgsConstructor
public class WithdrawalRequestController {

    private final WithdrawalRequestService withdrawalService;

    // Lấy danh sách các yêu cầu rút tiền của instructor
    @GetMapping
    public ResponseEntity<List<WithdrawalRequestDTO>> getInstructorWithdrawals(
            @PathVariable("instructorId") Long instructorId) {
        return ResponseEntity.ok(withdrawalService.getMyRequests(instructorId));
    }

    // Tạo yêu cầu rút tiền (gửi mã xác minh)
    @PostMapping("/create")
    public ResponseEntity<WithdrawalRequestDTO> createRequest(
            @PathVariable("instructorId") Long instructorId,
            @RequestBody WithdrawalRequestCreateRequest request) {
        return ResponseEntity.ok(withdrawalService.createRequest(instructorId, request));
    }

    // Xác minh yêu cầu rút tiền bằng token từ email
    @GetMapping("/verify")
    public ResponseEntity<String> verifyWithdrawal(@RequestParam("token") String token, @PathVariable String instructorId) {
        try {
            withdrawalService.verifyWithdrawal(token);

            String htmlSuccess = """
        <!DOCTYPE html>
        <html>
        <head><script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script></head>
        <body>
        <script>
            Swal.fire({
                icon: 'success',
                title: 'Verification Successful!',
                text: 'Your withdrawal request has been verified.',
                confirmButtonText: 'Go to Dashboard'
            }).then(() => {
                window.location.href = 'https://code-verse-web-fe.vercel.app/instructor-panel/manageBalance';
            });
        </script>
        </body>
        </html>
        """;

            return ResponseEntity.ok()
                    .header("Content-Type", "text/html")
                    .body(htmlSuccess);

        } catch (AppException e) {
            String htmlError = """
        <!DOCTYPE html>
        <html>
        <head><script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script></head>
        <body>
        <script>
            Swal.fire({
                icon: 'error',
                title: 'Verification Failed',
                text: 'The verification link is invalid or expired.',
            }).then(() => {
                window.location.href = 'https://code-verse-web-fe.vercel.app/instructor-panel/manageBalance';
            });
        </script>
        </body>
        </html>
        """;

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/html")
                    .body(htmlError);
        }
    }

    // Hủy yêu cầu rút tiền
    @DeleteMapping("/{requestId}/cancel")
    public ResponseEntity<String> cancelRequest(
            @PathVariable("instructorId") Long instructorId,
            @PathVariable("requestId") Long requestId) {
        withdrawalService.cancelRequest(requestId, instructorId);
        return ResponseEntity.ok("Request has been cancelled.");
    }
}

