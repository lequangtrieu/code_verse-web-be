package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);
    List<Course> findByInstructorUsername(String username);

    List<Course> findAllByIsDeletedFalseAndIsPublishedTrue();

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name as category, c.price, c.discount, " +
            "u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalLessons, " +
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c) as rating, " +
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c) as ratingCount, " +
            "(SELECT COUNT(DISTINCT pt.user) FROM CourseEnrollment pt WHERE pt.course = c) as totalStudents, " +
            "false as isTrending, " +
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalDurations) " +
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

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name as category, c.price, c.discount, " +
            "u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalLessons, " +
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c) as rating, " +
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c) as ratingCount, " +
            "(SELECT COUNT(DISTINCT pt2.user) FROM CourseEnrollment pt2 WHERE pt2.course = c) as totalStudents, " +
            "false as isTrending, " +
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalDurations) " +
            "FROM CourseEnrollment pt " +
            "JOIN pt.course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE pt.user.id = :userId " +
            "AND pt.completionPercentage < 100 " +
            "AND c.isDeleted = false AND c.isPublished = true")
    List<CourseResponse> findInProgressCourseResponsesByUserId(@Param("userId") Long userId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name as category, c.price, c.discount, " +
            "u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalLessons, " +
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c) as rating, " +
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c) as ratingCount, " +
            "(SELECT COUNT(DISTINCT pt2.user) FROM CourseEnrollment pt2 WHERE pt2.course = c) as totalStudents, " +
            "false as isTrending, " +
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalDurations) " +
            "FROM CourseEnrollment pt " +
            "JOIN pt.course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE pt.user.id = :userId " +
            "AND pt.completionPercentage >= 100 " +
            "AND c.isDeleted = false AND c.isPublished = true")
    List<CourseResponse> findCompletedCourseResponsesByUserId(@Param("userId") Long userId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name as category, c.price, c.discount, " +
            "u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalLessons, " +
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c) as rating, " +
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c) as ratingCount, " +
            "(SELECT COUNT(DISTINCT pt2.user) FROM CourseEnrollment pt2 WHERE pt2.course = c) as totalStudents, " +
            "false as isTrending, " +
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalDurations) " +
            "FROM Course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE c.isDeleted = false AND c.isPublished = true " +
            "AND NOT EXISTS (SELECT 1 FROM CourseEnrollment pt WHERE pt.course = c AND pt.user.id = :userId)")
    List<CourseResponse> findSuggestedCourseResponsesByUserId(@Param("userId") Long userId);




}