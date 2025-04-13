package codeverse.com.web_be.service.ProgressTrackingService;

import codeverse.com.web_be.entity.ProgressTracking;
import codeverse.com.web_be.repository.ProgressTrackingRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgressTrackingServiceImpl extends GenericServiceImpl<ProgressTracking, Long> implements IProgressTrackingService {

    private final ProgressTrackingRepository progressTrackingRepository;

    public ProgressTrackingServiceImpl(ProgressTrackingRepository progressTrackingRepository) {
        super(progressTrackingRepository);
        this.progressTrackingRepository = progressTrackingRepository;
    }

    @Override
    public List<ProgressTracking> findByUserId(Long userId) {
        return progressTrackingRepository.findByUserId(userId);
    }

    @Override
    public Optional<ProgressTracking> findByUserIdAndCourseId(Long userId, Long courseId) {
        return progressTrackingRepository.findByUserIdAndCourseId(userId, courseId);
    }
}