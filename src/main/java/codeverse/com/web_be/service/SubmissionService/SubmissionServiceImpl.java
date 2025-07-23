package codeverse.com.web_be.service.SubmissionService;

import codeverse.com.web_be.entity.CodeSubmission;
import codeverse.com.web_be.repository.CodeSubmissionRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SubmissionServiceImpl extends GenericServiceImpl<CodeSubmission, Long> implements ISubmissionService {

    private final CodeSubmissionRepository codeSubmissionRepository;

    public SubmissionServiceImpl(CodeSubmissionRepository codeSubmissionRepository) {
        super(codeSubmissionRepository);
        this.codeSubmissionRepository = codeSubmissionRepository;
    }
}