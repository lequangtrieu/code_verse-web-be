package codeverse.com.web_be.service.GradingService;

import codeverse.com.web_be.entity.Grading;
import codeverse.com.web_be.repository.GradingRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GradingServiceImpl extends GenericServiceImpl<Grading, Long> implements IGradingService {

    private final GradingRepository gradingRepository;

    public GradingServiceImpl(GradingRepository gradingRepository) {
        super(gradingRepository);
        this.gradingRepository = gradingRepository;
    }

    @Override
    public Optional<Grading> findBySubmissionId(Long submissionId) {
        return gradingRepository.findBySubmissionId(submissionId);
    }
}