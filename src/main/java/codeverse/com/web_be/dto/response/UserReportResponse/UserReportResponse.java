package codeverse.com.web_be.dto.response;

import codeverse.com.web_be.enums.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserReportResponse {

    private Long id;
    private Long reporterId;
    private String reporterUsername;
    private Long reportedUserId;
    private String reportedUsername;
    private Long reasonId;
    private String reasonTitle;
    private String customReason;
    private String evidenceUrl;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private Long messageId;
    private String adminNote;
}