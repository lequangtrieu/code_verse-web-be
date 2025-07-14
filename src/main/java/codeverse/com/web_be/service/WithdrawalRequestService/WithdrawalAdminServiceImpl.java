package codeverse.com.web_be.service.WithdrawalRequestService;

import codeverse.com.web_be.dto.response.WithdrawalResponse.WithdrawalRequestAdminDTO;
import codeverse.com.web_be.entity.WithdrawalRequest;
import codeverse.com.web_be.enums.WithdrawalStatus;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.WithdrawalRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class WithdrawalAdminServiceImpl implements WithdrawalAdminService {

    private final WithdrawalRequestRepository withdrawalRepo;

    @Override
    public List<WithdrawalRequestAdminDTO> getAllWithdrawalRequests(String name, String statusStr, LocalDateTime start, LocalDateTime end) {
        WithdrawalStatus status = null;
        if (statusStr != null) {
            try {
                status = WithdrawalStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                ignored.printStackTrace();
            }
        }

        return withdrawalRepo.filterAll(status, name, start, end)
                .stream()
                .map(req -> WithdrawalRequestAdminDTO.builder()
                        .id(req.getId())
                        .instructorId(req.getInstructor().getId())
                        .instructorName(req.getInstructor().getName())
                        .qrCodeUrl(req.getInstructor().getQrCodeUrl())
                        .amount(req.getAmount())
                        .status(req.getStatus())
                        .adminNote(req.getAdminNote())
                        .createdAt(req.getCreatedAt())
                        .paymentMethod("Bank Transfer") // hoặc lấy từ đâu đó nếu có field
                        .build()
                ).collect(toList());
    }

    @Override
    public void approveWithdrawal(Long requestId) {
        WithdrawalRequest request = withdrawalRepo.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_EXISTED));

        if (request.getStatus() != WithdrawalStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }

        request.setStatus(WithdrawalStatus.APPROVED);
        withdrawalRepo.save(request);
    }

    @Override
    public void rejectWithdrawal(Long requestId, String reason) {
        WithdrawalRequest request = withdrawalRepo.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_EXISTED));

        if (request.getStatus() != WithdrawalStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }

        request.setStatus(WithdrawalStatus.REJECTED);
        request.setAdminNote(reason);
        withdrawalRepo.save(request);
    }
}
