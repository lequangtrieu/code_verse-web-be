package codeverse.com.web_be.dto.request.ReportRequest;

import lombok.Data;

@Data
public class UserReportRequest {
    private Long reportedUserId;
    private Long reasonId;
    private String customReason;
    private String evidenceUrl;
    private Long messageId;
}
