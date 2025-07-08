package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.ReportRequest.UpdateUserReportRequest;
import codeverse.com.web_be.dto.request.ReportRequest.UserReportRequest;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.dto.response.UserReportResponse;
import codeverse.com.web_be.entity.UserReport;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.UserReportService.IUserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-reports")
@RequiredArgsConstructor
public class UserReportController {

    private final IUserReportService userReportService;
    private final FirebaseStorageService firebaseStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserReport> createUserReport(
            @RequestParam("reportedUserId") Long reportedUserId,
            @RequestParam("reasonId") Long reasonId,
            @RequestParam(value = "customReason", required = false) String customReason,
            @RequestParam(value = "messageId", required = false) Long messageId,
            @RequestPart(value = "evidence", required = false) MultipartFile evidence
    ) {
        String evidenceUrl = evidence != null ? firebaseStorageService.uploadImage(evidence) : null;

        UserReportRequest request = new UserReportRequest();
        request.setReportedUserId(reportedUserId);
        request.setReasonId(reasonId);
        request.setCustomReason(customReason);
        request.setMessageId(messageId);
        request.setEvidenceUrl(evidenceUrl);

        UserReport report = userReportService.createReport(request);

        return ApiResponse.<UserReport>builder()
                .result(report)
                .message("Report submitted successfully.")
                .build();
    }

    @GetMapping("list-report")
    public List<UserReportResponse> getAllReports() {
        return userReportService.getAllReports();
    }

    @PatchMapping("admin-review/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @RequestBody UpdateUserReportRequest request
    ) {
        userReportService.updateReport(id, request);
        return ResponseEntity.ok().build();
    }
}