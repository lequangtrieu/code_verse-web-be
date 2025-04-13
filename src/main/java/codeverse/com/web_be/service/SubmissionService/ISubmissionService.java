package codeverse.com.web_be.service.SubmissionService;

import codeverse.com.web_be.entity.Submission;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface ISubmissionService extends IGenericService<Submission, Long> {
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByLearnerId(Long learnerId);
}