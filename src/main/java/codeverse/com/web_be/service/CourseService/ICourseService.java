package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface ICourseService extends IGenericService<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);
}