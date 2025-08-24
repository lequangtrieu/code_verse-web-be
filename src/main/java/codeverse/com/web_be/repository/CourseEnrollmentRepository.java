package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.MonthlyLearnerStatisticResponse;
import codeverse.com.web_be.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findByUserId(Long userId);

    Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    List<CourseEnrollment> findByCourseId(Long courseId);

    boolean existsByUserId(Long userId);

    @Query("""
                SELECT new codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.MonthlyLearnerStatisticResponse(
                    c.id,
                    c.title,
                    MONTH(e.createdAt),
                    YEAR(e.createdAt),
                    COUNT(e.id)
                )
                FROM CourseEnrollment e
                JOIN e.course c
                WHERE c.instructor.id = :instructorId
                GROUP BY c.id, c.title, YEAR(e.createdAt), MONTH(e.createdAt)
                HAVING COUNT(e.id) > 0
                ORDER BY YEAR(e.createdAt), MONTH(e.createdAt)
            """)
    List<MonthlyLearnerStatisticResponse> findMonthlyEnrollmentStatsByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT pt.completionPercentage FROM CourseEnrollment pt " +
            "WHERE pt.course.id = :courseId AND pt.user.id = :userId")
    Float getCompletionPercentage(@Param("courseId") Long courseId, @Param("userId") Long userId);

    @Query("SELECT ce FROM CourseEnrollment ce " +
            "WHERE ce.user.id = :userId AND ce.completionPercentage = :percentage")
    List<CourseEnrollment> findByUserIdAndCompletionPercentage(
            @Param("userId") Long userId,
            @Param("percentage") Float percentage);

    boolean existsByUserUsernameAndCourseId(String username, Long courseId);
    int countByUserIdAndCompletionPercentageGreaterThanEqual(Long userId, Float completionPercentage);
}