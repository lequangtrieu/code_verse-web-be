package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<CodeSubmission, Long> {
}