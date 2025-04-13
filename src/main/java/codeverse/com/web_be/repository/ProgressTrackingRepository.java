package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.ProgressTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProgressTrackingRepository extends JpaRepository<ProgressTracking, Long> {
    List<ProgressTracking> findByUserId(Long userId);
    Optional<ProgressTracking> findByUserIdAndCourseId(Long userId, Long courseId);
}