package codeverse.com.web_be.service.ReportReasonService;

import codeverse.com.web_be.dto.request.ReportReasonRequest.ReportReasonRequest;
import codeverse.com.web_be.dto.response.ReportReasonResponse.ReportReasonResponse;
import codeverse.com.web_be.entity.ReportReason;

import java.util.List;

public interface IReportReasonService {
    List<ReportReason> getAllReasons();
    ReportReasonResponse getById(Long id);
    ReportReasonResponse create(ReportReasonRequest request);
    ReportReasonResponse update(Long id, ReportReasonRequest request);
    void hide(Long id);
    void unhide(Long id);
}
