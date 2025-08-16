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

    @Query("""
        SELECT COUNT(l)
        FROM CourseEnrollment ce
        JOIN ce.course c
        JOIN c.courseModules cm
        JOIN cm.lessons l
        WHERE ce.user.id = :userId
    """)
    long countLessonsByUserId(@Param("userId") Long userId);
}
