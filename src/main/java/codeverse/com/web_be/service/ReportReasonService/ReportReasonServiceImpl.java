package codeverse.com.web_be.service.ReportReasonService;

import codeverse.com.web_be.dto.request.ReportReasonRequest.ReportReasonRequest;
import codeverse.com.web_be.dto.response.ReportReasonResponse.ReportReasonResponse;
import codeverse.com.web_be.entity.ReportReason;
import codeverse.com.web_be.repository.ReportReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportReasonServiceImpl implements IReportReasonService {

    private final ReportReasonRepository reportReasonRepository;

    @Override
    public List<ReportReason> getAllReasons() {
        return reportReasonRepository.findAll();
    }

    public List<ReportReason> getActiveReasons() {
        return reportReasonRepository.findByActiveTrue();
    }

    @Override
    public ReportReasonResponse getById(Long id) {
        ReportReason reason = reportReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report reason not found"));
        return mapToResponse(reason);
    }

    @Override
    public ReportReasonResponse create(ReportReasonRequest request) {
        ReportReason reason = ReportReason.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .active(true)
                .build();
        return mapToResponse(reportReasonRepository.save(reason));
    }

    @Override
    public ReportReasonResponse update(Long id, ReportReasonRequest request) {
        ReportReason reason = reportReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report reason not found"));
        reason.setTitle(request.getTitle());
        reason.setDescription(request.getDescription());
        return mapToResponse(reportReasonRepository.save(reason));
    }

    @Override
    public void hide(Long id) {
        ReportReason reason = reportReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report reason not found"));
        reason.setActive(false);
        reportReasonRepository.save(reason);
    }

    private ReportReasonResponse mapToResponse(ReportReason reason) {
        return ReportReasonResponse.builder()
                .id(reason.getId())
                .title(reason.getTitle())
                .description(reason.getDescription())
                .active(reason.getActive())
                .build();
    }

    @Override
    public void unhide(Long id) {
        ReportReason reason = reportReasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report reason not found"));
        reason.setActive(true);
        reportReasonRepository.save(reason);
    }

}
