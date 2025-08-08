package codeverse.com.web_be.service.WithdrawalRequestService;

import codeverse.com.web_be.dto.response.WithdrawalResponse.WithdrawalRequestAdminDTO;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.entity.WithdrawalRequest;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.enums.WithdrawalStatus;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.UserRepository;
import codeverse.com.web_be.repository.WithdrawalRequestRepository;
import codeverse.com.web_be.service.EmailService.EmailServiceSender;
import codeverse.com.web_be.service.NotificationService.INotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class WithdrawalAdminServiceImpl implements WithdrawalAdminService {

    private final WithdrawalRequestRepository withdrawalRepo;
    private final EmailServiceSender emailSender;
    private final INotificationService notificationService;
    private final UserRepository userRepository;

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

        try {
            emailSender.sendWithdrawalConfirmationEmail(request);

            List<User> admins = userRepository.findAll().stream()
                    .filter(u -> u.getRole().equals(UserRole.ADMIN))
                    .toList();
            notificationService.notifyUsers(List.of(request.getInstructor()), admins.get(0),
                    "Withdrawal Request Approved",
                    "<p><b>Dear " + request.getInstructor().getName() + ",</b></p>" +
                            "<p>Your withdrawal request for <strong>" + request.getAmount() + " VND</strong> has been approved by the admin.</p>" +
                            "<p>To complete the process, please check your email and confirm the withdrawal by clicking the confirmation link.</p>" +
                            "<p>Sincerely,<br/>The Admin Team</p>");
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
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

        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole().equals(UserRole.ADMIN))
                .toList();
        String note = request.getAdminNote();
        String reasonHtml = (note != null && !note.isBlank())
                ? "<p><b>Reason:</b> " + note + "</p>"
                : "";
        notificationService.notifyUsers(
                List.of(request.getInstructor()),
                admins.get(0),
                "Withdrawal Request Rejected",
                "<p><b>Dear " + request.getInstructor().getName() + ",</b></p>" +
                        "<p>We regret to inform you that your withdrawal request for <strong>" + request.getAmount() + " VND</strong> has been rejected by the admin.</p>" +
                        reasonHtml +
                        "<p>If you have any questions or believe this was a mistake, feel free to <a href=\"mailto:dolvapple@gmail.com\">contact us via email</a>.</p>" +
                        "<p>Thank you for your understanding.</p>" +
                        "<p>Sincerely,<br/>The Admin Team</p>"
        );
    }
}
