package codeverse.com.web_be.service.UserReportService;

import codeverse.com.web_be.dto.request.ReportRequest.UserReportRequest;
import codeverse.com.web_be.entity.UserReport;

public interface IUserReportService {
    UserReport createReport(UserReportRequest request);
}
