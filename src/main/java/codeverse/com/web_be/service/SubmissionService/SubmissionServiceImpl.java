package codeverse.com.web_be.service.SubmissionService;

import codeverse.com.web_be.entity.Submission;
import codeverse.com.web_be.repository.SubmissionRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionServiceImpl extends GenericServiceImpl<Submission, Long> implements ISubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        super(submissionRepository);
        this.submissionRepository = submissionRepository;
    }

    @Override
    public List<Submission> findByAssignmentId(Long assignmentId) {
        return submissionRepository.findByExerciseId(assignmentId);
    }

    @Override
    public List<Submission> findByLearnerId(Long learnerId) {
        return submissionRepository.findByLearnerId(learnerId);
    }
}