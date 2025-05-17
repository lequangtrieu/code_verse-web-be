package codeverse.com.web_be.service.SubmissionService;

import codeverse.com.web_be.entity.CodeSubmission;
import codeverse.com.web_be.repository.SubmissionRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SubmissionServiceImpl extends GenericServiceImpl<CodeSubmission, Long> implements ISubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        super(submissionRepository);
        this.submissionRepository = submissionRepository;
    }
}