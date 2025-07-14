package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.WithdrawalRequest.WithdrawalRejectRequest;
import codeverse.com.web_be.dto.response.WithdrawalResponse.WithdrawalRequestAdminDTO;
import codeverse.com.web_be.service.WithdrawalRequestService.WithdrawalAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/withdrawals")
@RequiredArgsConstructor
public class WithdrawalAdminController {

    private final WithdrawalAdminService withdrawalAdminService;

    @GetMapping
    public ResponseEntity<List<WithdrawalRequestAdminDTO>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(withdrawalAdminService.getAllWithdrawalRequests(name, status, start, end));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        withdrawalAdminService.approveWithdrawal(id);
        return ResponseEntity.ok("Withdrawal approved");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable Long id, @RequestBody WithdrawalRejectRequest request) {
        withdrawalAdminService.rejectWithdrawal(id, request.getReason());
        return ResponseEntity.ok("Withdrawal rejected");
    }
}

