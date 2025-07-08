package codeverse.com.web_be.service.UserReportService;

import codeverse.com.web_be.dto.request.ReportRequest.UpdateUserReportRequest;
import codeverse.com.web_be.dto.request.ReportRequest.UserReportRequest;
import codeverse.com.web_be.dto.response.UserReportResponse;
import codeverse.com.web_be.entity.DiscussionMessage;
import codeverse.com.web_be.entity.ReportReason;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.entity.UserReport;
import codeverse.com.web_be.enums.ReportStatus;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.DiscussionMessageRepository;
import codeverse.com.web_be.repository.ReportReasonRepository;
import codeverse.com.web_be.repository.UserReportRepository;
import codeverse.com.web_be.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReportServiceImpl implements IUserReportService {

    private final UserRepository userRepository;
    private final ReportReasonRepository reportReasonRepository;
    private final UserReportRepository userReportRepository;
    private final DiscussionMessageRepository discussionMessageRepository;

    @Override
    public UserReport createReport(UserReportRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Received report: " + request);

        User reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        User reportedUser = userRepository.findById(request.getReportedUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        ReportReason reason = reportReasonRepository.findById(request.getReasonId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REPORT_REASON));

        DiscussionMessage message = null;
        if (request.getMessageId() != null) {
            System.out.println("Looking up messageId = " + request.getMessageId());
            message = discussionMessageRepository.findById(request.getMessageId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_EXISTED));
        }

        UserReport report = UserReport.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(reason)
                .customReason(request.getCustomReason())
                .evidenceUrl(request.getEvidenceUrl())
                .status(ReportStatus.PENDING)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        return userReportRepository.save(report);
    }

    @Override
    public List<UserReportResponse> getAllReports() {
        List<UserReport> reports = userReportRepository.findAll();

        return reports.stream().map(report -> UserReportResponse.builder()
                .id(report.getId())
                .reporterId(report.getReporter().getId())
                .reporterUsername(report.getReporter().getUsername())
                .reportedUserId(report.getReportedUser().getId())
                .reportedUsername(report.getReportedUser().getUsername())
                .reasonId(report.getReason().getId())
                .reasonTitle(report.getReason().getTitle())
                .customReason(report.getCustomReason())
                .evidenceUrl(report.getEvidenceUrl())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .messageId(report.getMessage() != null ? report.getMessage().getId() : null)
                .adminNote(report.getAdminNote())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateReport(Long reportId, UpdateUserReportRequest request) {
        UserReport report = userReportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_EXISTED));

        report.setStatus(request.getStatus());
        report.setAdminNote(request.getAdminNote());
        report.setReviewedAt(LocalDateTime.now());

        userReportRepository.save(report);
    }
}