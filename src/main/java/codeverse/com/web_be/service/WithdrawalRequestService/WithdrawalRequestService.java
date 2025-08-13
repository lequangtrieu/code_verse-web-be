package codeverse.com.web_be.service.WithdrawalRequestService;

import codeverse.com.web_be.dto.request.WithdrawalRequest.WithdrawalRequestCreateRequest;
import codeverse.com.web_be.dto.response.WithdrawalResponse.WithdrawalRequestDTO;
import jakarta.mail.MessagingException;

import java.util.List;

public interface WithdrawalRequestService {
    WithdrawalRequestDTO createRequest(Long instructorId, WithdrawalRequestCreateRequest request);
    List<WithdrawalRequestDTO> getMyRequests(Long instructorId);
    void verifyWithdrawal(String token);
    void cancelRequest(Long requestId, Long instructorId);
    void confirmWithdrawal(Long requestId);
}
