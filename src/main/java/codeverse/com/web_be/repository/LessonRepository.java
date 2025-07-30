package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseModuleId(Long courseModuleId);
    List<Lesson> findByCourseModuleIdOrderByOrderIndexAsc(Long courseModuleId);
    List<Lesson> findByCourseModule_Course_Id(Long courseId);
    Optional<Lesson> findFirstByCourseModule_Course_IdOrderByCourseModule_OrderIndexAscOrderIndexAsc(Long courseId);
}
