package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByAssignmentId(Long assignmentId);
}