package codeverse.com.web_be.service.WithdrawalRequestService;

import codeverse.com.web_be.dto.response.WithdrawalResponse.WithdrawalRequestAdminDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface WithdrawalAdminService {
    List<WithdrawalRequestAdminDTO> getAllWithdrawalRequests(String name, String status, LocalDateTime start, LocalDateTime end);
    void approveWithdrawal(Long requestId);
    void rejectWithdrawal(Long requestId, String reason);
}
