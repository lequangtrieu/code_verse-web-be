package codeverse.com.web_be.service.CourseEnrollmentService;

import codeverse.com.web_be.dto.response.CourseEnrollmentResponse.CertificateInfoDTO;
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

    @Override
    public List<CourseEnrollment> getCompletedCoursesByUserId(Long userId) {
        return courseEnrollmentRepository
                .findByUserIdAndCompletionPercentage(userId, 100f);
    }

    @Override
    public CertificateInfoDTO getCertificateInfo(Long userId, Long courseId) {
        CourseEnrollment enrollment = courseEnrollmentRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (enrollment.getCompletionPercentage() < 100.0f) {
            throw new RuntimeException("Course not completed");
        }

        return new CertificateInfoDTO(
                enrollment.getUser().getUsername(),
                enrollment.getCourse().getTitle(),
                enrollment.getCourse().getInstructor().getUsername(),
                enrollment.getUpdatedAt()
        );
    }

    @Override
    public boolean isUserEnrolled(Long courseId, String username) {
        return courseEnrollmentRepository.existsByUserUsernameAndCourseId(username, courseId);
    }

}