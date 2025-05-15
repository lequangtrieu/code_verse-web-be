package codeverse.com.web_be.service.CourseEnrollmentService;

import codeverse.com.web_be.entity.CourseEnrollment;
import codeverse.com.web_be.repository.CourseEnrollmentRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseEnrollmentServiceImpl extends GenericServiceImpl<CourseEnrollment, Long> implements ICourseEnrollmentService {

    private final CourseEnrollmentRepository courseEnrollmentRepository;

    public CourseEnrollmentServiceImpl(CourseEnrollmentRepository courseEnrollmentRepository) {
        super(courseEnrollmentRepository);
        this.courseEnrollmentRepository = courseEnrollmentRepository;
    }

    @Override
    public List<CourseEnrollment> findByUserId(Long userId) {
        return courseEnrollmentRepository.findByUserId(userId);
    }

    @Override
    public Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId) {
        return courseEnrollmentRepository.findByUserIdAndCourseId(userId, courseId);
    }
}