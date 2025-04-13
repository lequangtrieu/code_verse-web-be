package codeverse.com.web_be.service.ProgressTrackingService;

import codeverse.com.web_be.entity.ProgressTracking;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;
import java.util.Optional;

public interface IProgressTrackingService extends IGenericService<ProgressTracking, Long> {
    List<ProgressTracking> findByUserId(Long userId);
    Optional<ProgressTracking> findByUserIdAndCourseId(Long userId, Long courseId);
}