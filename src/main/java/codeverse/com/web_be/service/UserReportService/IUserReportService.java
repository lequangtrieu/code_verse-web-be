package codeverse.com.web_be.service.UserReportService;

import codeverse.com.web_be.dto.request.ReportRequest.UpdateUserReportRequest;
import codeverse.com.web_be.dto.request.ReportRequest.UserReportRequest;
import codeverse.com.web_be.dto.response.UserReportResponse;
import codeverse.com.web_be.entity.UserReport;

import java.util.List;

public interface IUserReportService {
    UserReport createReport(UserReportRequest request);
    List<UserReportResponse> getAllReports();
    void updateReport(Long reportId, UpdateUserReportRequest request);
}
