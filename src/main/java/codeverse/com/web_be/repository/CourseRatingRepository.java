package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.CourseRating;
import codeverse.com.web_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {
    Optional<CourseRating> findByUserAndCourse(User user, Course course);
    List<CourseRating> findByCourse(Course course);
} 