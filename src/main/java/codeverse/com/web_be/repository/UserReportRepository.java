package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findAllByOrderByCreatedAtDesc();
}
