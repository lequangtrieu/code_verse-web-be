package codeverse.com.web_be.service.ReportReasonService;

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
}
