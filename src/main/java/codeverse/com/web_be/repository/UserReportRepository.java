package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
