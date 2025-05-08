package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);

    List<Course> findAllByIsDeletedFalseAndIsPublishedTrue();

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name as category, c.price, c.discount, " +
            "u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.materialSection ms WHERE ms.course = c) as totalLessons, " +
            "4.7f as rating, 38 as ratingCount, null as totalStudents, null as isTrending, " +
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.materialSection ms WHERE ms.course = c) as totalDurations) " +
            "FROM Course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE (:id IS NULL OR c.id = :id) AND c.isDeleted = false AND c.isPublished = true")
    List<CourseResponse> selectCourses(@Param("id") Long id);

    default List<CourseResponse> selectAllCourses() {
        return selectCourses(null);
    }

    default CourseResponse selectCourseById(Long id) {
        List<CourseResponse> results = selectCourses(id);
        return results.isEmpty() ? null : results.get(0);
    }


}