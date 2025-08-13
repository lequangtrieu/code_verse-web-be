package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportReasonRepository extends JpaRepository<ReportReason, Long> {
    List<ReportReason> findByActiveTrue();
}
