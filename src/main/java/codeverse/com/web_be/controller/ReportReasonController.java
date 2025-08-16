package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.ReportReasonRequest.ReportReasonRequest;
import codeverse.com.web_be.dto.response.ReportReasonResponse.ReportReasonResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.ReportReason;
import codeverse.com.web_be.service.ReportReasonService.ReportReasonServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ReportReason>>> getActiveReportReasons() {
        return ResponseEntity.ok(
                ApiResponse.<List<ReportReason>>builder()
                        .result(reportReasonService.getActiveReasons()) // gọi service mới
                        .message("Success")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportReasonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reportReasonService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ReportReasonResponse> create(@RequestBody ReportReasonRequest request) {
        return ResponseEntity.ok(reportReasonService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportReasonResponse> update(@PathVariable Long id, @RequestBody ReportReasonRequest request) {
        return ResponseEntity.ok(reportReasonService.update(id, request));
    }

    @PutMapping("/{id}/hide")
    public ResponseEntity<Void> hide(@PathVariable Long id) {
        reportReasonService.hide(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unhide")
    public ResponseEntity<Void> unhide(@PathVariable Long id) {
        reportReasonService.unhide(id);
        return ResponseEntity.noContent().build();
    }
}
