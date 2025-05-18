package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {
    List<CourseModule> findByCourseId(Long courseId);
    CourseModule findById(long id);
}
