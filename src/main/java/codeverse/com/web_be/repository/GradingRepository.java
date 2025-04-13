package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Grading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GradingRepository extends JpaRepository<Grading, Long> {
    Optional<Grading> findBySubmissionId(Long submissionId);
}