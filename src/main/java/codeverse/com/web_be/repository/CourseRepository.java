package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUserId(Long instructorId);
}