package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);

    List<Course> findAllByIsDeletedFalseAndIsPublishedTrue();

//    @Query("SELECT COUNT(l) FROM Lesson l " +
//            "JOIN l.materialSection ms " +
//            "JOIN ms.course c " +
//            "WHERE c.id = :courseId")
//    Long countLessonsByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name as category, c.price, c.discount, " +
            "u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.materialSection ms WHERE ms.course = c), " +
            "4.7f as rating, 38 as ratingCount, null as totalStudents, null as isTrending, 90 as totalLessons) " +
            "FROM Course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE c.isDeleted = false AND c.isPublished = true") // thêm where isPaid = false vào đây
    List<CourseResponse> selectAllCourses();
}