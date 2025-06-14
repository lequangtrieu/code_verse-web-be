package codeverse.com.web_be.service.CourseEnrollmentService;

import codeverse.com.web_be.entity.CourseEnrollment;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;
import java.util.Optional;

public interface ICourseEnrollmentService extends IGenericService<CourseEnrollment, Long> {
    List<CourseEnrollment> findByUserId(Long userId);
    Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}