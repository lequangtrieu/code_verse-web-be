package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.CourseRating;
import codeverse.com.web_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {
    Optional<CourseRating> findByUserAndCourse(User user, Course course);
    List<CourseRating> findByCourse(Course course);
    @Query("SELECT cr FROM CourseRating cr WHERE cr.user.id = :userId AND cr.course.id = :courseId")
    Optional<CourseRating> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
} 