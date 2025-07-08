package codeverse.com.web_be.dto.request.ReportRequest;

import codeverse.com.web_be.enums.ReportStatus;
import lombok.Data;

@Data
public class UpdateUserReportRequest {
    private ReportStatus status;
    private String adminNote;
}
