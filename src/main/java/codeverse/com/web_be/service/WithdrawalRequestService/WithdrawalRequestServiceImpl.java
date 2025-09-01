package codeverse.com.web_be.service.WithdrawalRequestService;

import codeverse.com.web_be.dto.request.WithdrawalRequest.WithdrawalRequestCreateRequest;
import codeverse.com.web_be.dto.response.WithdrawalResponse.WithdrawalRequestDTO;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class WithdrawalRequestServiceImpl implements WithdrawalRequestService {

    private final WithdrawalRequestRepository withdrawalRepo;
    private final UserRepository userRepo;
    private final EmailServiceSender emailSender;
    private final INotificationService notificationService;

    @Override
    @Transactional
    public WithdrawalRequestDTO createRequest(Long instructorId, WithdrawalRequestCreateRequest request) {
        User instructor = userRepo.findById(instructorId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        boolean exists = withdrawalRepo.existsByInstructorAndStatusIn(
                instructor,
                List.of(WithdrawalStatus.PENDING, WithdrawalStatus.NEED_VERIFY)
        );

        if (exists) {
            throw new AppException(ErrorCode.WITHDRAWAL_REQUEST_ALREADY_EXISTS);
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.valueOf(20000)) < 0) {
            throw new AppException(ErrorCode.WITHDRAWAL_AMOUNT_TOO_LOW);
        }

        String verifyToken = UUID.randomUUID().toString();

        WithdrawalRequest entity = WithdrawalRequest.builder()
                .instructor(instructor)
                .amount(request.getAmount())
                .status(WithdrawalStatus.NEED_VERIFY)
                .verifyToken(verifyToken)
                .build();

        withdrawalRepo.save(entity);

        try {
            emailSender.sendWithdrawalVerificationEmail(instructor.getUsername(), verifyToken, request.getAmount(), instructorId);
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return toDTO(entity);
    }

    @Override
    public List<WithdrawalRequestDTO> getMyRequests(Long instructorId) {
        User instructor = new User(instructorId);
        return withdrawalRepo.findByInstructor(instructor).stream()
                .map(this::toDTO)
                .collect(toList());
    }

    @Override
    @Transactional
    public void verifyWithdrawal(String token) {
        WithdrawalRequest request = withdrawalRepo.findByVerifyToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAWAL_INVALID_TOKEN));

        if (request.getStatus() != WithdrawalStatus.NEED_VERIFY) {
            throw new AppException(ErrorCode.WITHDRAWAL_INVALID_STATUS);
        }

        request.setStatus(WithdrawalStatus.PENDING);
        request.setVerifyToken(null);
        withdrawalRepo.save(request);

        List<User> admins = userRepo.findAll().stream()
                .filter(u -> u.getRole().equals(UserRole.ADMIN))
                .toList();
        notificationService.notifyUsers(admins, request.getInstructor(), "New Withdrawal Request",
                "<p>" + request.getInstructor().getName() + " has sent a  withdrawal request. " +
                        "<a href=\"https://code-verse-web-fe.vercel.app/admin-panel/withdrawalRequests" +
                        "\">View Request >></a><p/>");
    }

    @Override
    @Transactional
    public void cancelRequest(Long requestId, Long instructorId) {
        WithdrawalRequest request = withdrawalRepo.findById(requestId)
                .orElseThrow(() ->  new AppException(ErrorCode.WITHDRAWAL_NOT_FOUND));

        if (!request.getInstructor().getId().equals(instructorId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (request.getStatus() != WithdrawalStatus.PENDING &&
                request.getStatus() != WithdrawalStatus.NEED_VERIFY) {
            throw new AppException(ErrorCode.WITHDRAWAL_UNAUTHORIZED_CANCEL);
        }

        request.setStatus(WithdrawalStatus.CANCELLED);
        request.setAdminNote("Cancelled by instructor");
        withdrawalRepo.save(request);
    }

    @Override
    @Transactional
    public void confirmWithdrawal(Long requestId) {
        WithdrawalRequest request = withdrawalRepo.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_EXISTED));

        if (request.getStatus() != WithdrawalStatus.APPROVED) {
            throw new IllegalStateException("Only approved requests can be confirmed");
        }

        request.setStatus(WithdrawalStatus.CONFIRMED);
        withdrawalRepo.save(request);
    }

    private WithdrawalRequestDTO toDTO(WithdrawalRequest entity) {
        return WithdrawalRequestDTO.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .reviewedAt(entity.getReviewedAt())
                .adminNote(entity.getAdminNote())
                .build();
    }
}
