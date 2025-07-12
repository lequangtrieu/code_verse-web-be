package codeverse.com.web_be.service.CourseEnrollmentService;

import codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.MonthlyLearnerStatisticResponse;
import codeverse.com.web_be.entity.CourseEnrollment;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.repository.CourseEnrollmentRepository;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseEnrollmentServiceImpl extends GenericServiceImpl<CourseEnrollment, Long> implements ICourseEnrollmentService {

    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final FunctionHelper functionHelper;

    public CourseEnrollmentServiceImpl(CourseEnrollmentRepository courseEnrollmentRepository,
                                       FunctionHelper functionHelper) {
        super(courseEnrollmentRepository);
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.functionHelper = functionHelper;
    }

    @Override
    public List<CourseEnrollment> findByUserId(Long userId) {
        return courseEnrollmentRepository.findByUserId(userId);
    }

    @Override
    public Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId) {
        return courseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public List<MonthlyLearnerStatisticResponse> getMonthlyStats(String username) {
        User instructor = functionHelper.getActiveUserByUsername(username);
        return courseEnrollmentRepository.findMonthlyEnrollmentStatsByInstructorId(instructor.getId());
    }

    @Override
    public Float getUserCompletionPercentage(Long userId, Long courseId) {
        Float result = courseEnrollmentRepository.getCompletionPercentage(courseId, userId);
        return result != null ? result : 0f;
    }
}