package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.WithdrawalRequest.WithdrawalRequestCreateRequest;
import codeverse.com.web_be.dto.response.WithdrawalResponse.WithdrawalRequestDTO;
import codeverse.com.web_be.service.WithdrawalRequestService.WithdrawalRequestService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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
            @RequestBody WithdrawalRequestCreateRequest request) throws MessagingException {
        return ResponseEntity.ok(withdrawalService.createRequest(instructorId, request));
    }

    // Xác minh yêu cầu rút tiền bằng token từ email
    @GetMapping("/verify")
    public ResponseEntity<String> verifyWithdrawal(@RequestParam("token") String token) {
        withdrawalService.verifyWithdrawal(token);
        return ResponseEntity.ok("Verification successful.");
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

