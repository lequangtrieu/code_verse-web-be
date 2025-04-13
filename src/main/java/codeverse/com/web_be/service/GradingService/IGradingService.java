package codeverse.com.web_be.service.GradingService;

import codeverse.com.web_be.entity.Grading;
import codeverse.com.web_be.service.IGenericService;

import java.util.Optional;

public interface IGradingService extends IGenericService<Grading, Long> {
    Optional<Grading> findBySubmissionId(Long submissionId);
}