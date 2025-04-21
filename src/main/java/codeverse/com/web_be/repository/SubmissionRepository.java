package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByExerciseId(Long exerciseId);
    List<Submission> findByLearnerId(Long learnerId);
}