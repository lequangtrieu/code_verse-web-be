package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.ReportReason;
import codeverse.com.web_be.service.ReportReasonService.ReportReasonServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report-reasons")
@RequiredArgsConstructor
public class ReportReasonController {

    private final ReportReasonServiceImpl reportReasonService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ReportReason>>> getAllReportReasons() {
        return ResponseEntity.ok(
                ApiResponse.<List<ReportReason>>builder()
                        .result(reportReasonService.getAllReasons())
                        .message("Success")
                        .build()
        );
    }
}
