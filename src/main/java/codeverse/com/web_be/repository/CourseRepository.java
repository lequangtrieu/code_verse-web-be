package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);
    List<Course> findByInstructorUsername(String username);

//    List<Course> findAllByIsDeletedFalseAndIsPublishedTrue();

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
            "WHERE c.isDeleted = false AND c.status = \"PUBLISHED\"")
    List<CourseResponse> selectAllCourses();

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
            "WHERE c.id = :id AND c.isDeleted = false AND c.status = \"PUBLISHED\"")
    CourseResponse selectCourseById(Long id);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name, u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " +              // totalLessons
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c), " + // rating
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c), " +                             // ratingCount
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " + // totalDurations
            "pt.completionPercentage) " +                                                                // completionPercentage
            "FROM CourseEnrollment pt " +
            "JOIN pt.course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE pt.user.id = :userId " +
            "AND pt.completionPercentage < 100 " +
            "AND c.isDeleted = false AND c.status = 'PUBLISHED'")
    List<CourseProgressResponse> findInProgressCourseResponsesByUserId(@Param("userId") Long userId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name, u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " +              // totalLessons
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c), " + // rating
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c), " +                             // ratingCount
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " + // totalDurations
            "pt.completionPercentage) " +                                                                // completionPercentage
            "FROM CourseEnrollment pt " +
            "JOIN pt.course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE pt.user.id = :userId " +
            "AND pt.completionPercentage = 100 " +
            "AND c.isDeleted = false AND c.status = 'PUBLISHED'")
    List<CourseProgressResponse> findCompletedCourseResponsesByUserId(@Param("userId") Long userId);

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
            "WHERE c.isDeleted = false AND c.status = \"PUBLISHED\" " +
            "AND NOT EXISTS (SELECT 1 FROM CourseEnrollment pt WHERE pt.course = c AND pt.user.id = :userId)")
    List<CourseResponse> findSuggestedCourseResponsesByUserId(@Param("userId") Long userId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name, u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " +              // totalLessons
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c), " + // rating
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c), " +                             // ratingCount
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " + // totalDurations
            "pt.completionPercentage) " +                                                                // completionPercentage
            "FROM CourseEnrollment pt " +
            "JOIN pt.course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE pt.user.id = :userId " +
            "AND c.isDeleted = false AND c.status = 'PUBLISHED'")
    List<CourseProgressResponse> findAllCoursesWithProgressByUserId(@Param("userId") Long userId);

}