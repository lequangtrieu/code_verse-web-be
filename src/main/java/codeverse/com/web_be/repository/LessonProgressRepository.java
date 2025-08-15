package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.LessonProgressDTO.LessonProgressDTO;
import codeverse.com.web_be.dto.response.RankingResponse.RankingDTO;
import codeverse.com.web_be.entity.LessonProgress;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.LessonProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("SELECT lp FROM LessonProgress lp " +
            "WHERE lp.user.id = :userId AND lp.lesson.id = :lessonId " +
            "ORDER BY lp.startedAt DESC")
    List<LessonProgress> findAllAttemptsOrderByStartedDesc(@Param("userId") Long userId, @Param("lessonId") Long lessonId);

    @Query("SELECT new codeverse.com.web_be.dto.response.LessonProgressDTO.LessonProgressDTO(" +
            "lp.id as id," +
            "lp.user.id as userId, " +
            "lp.lesson.id as lessonId, " +
            "lp.expGained as expGained, " +
            "lp.status as status, " +
            "lp.startedAt as startedAt, " +
            "lp.completedAt as completedAt) " +
            "FROM LessonProgress lp " +
            "WHERE lp.user.id = :userId AND lp.lesson.id = :lessonId")
    List<LessonProgressDTO> findByUserIdAndLessonIdQuery(@Param("userId") Long userId, @Param("lessonId") Long lessonId);

    List<LessonProgress> findAllByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("""
                SELECT lp FROM LessonProgress lp
                WHERE lp.user.id = :userId
                  AND lp.lesson.courseModule.course.id = :courseId
                  AND lp.status = :status
            """)
    List<LessonProgress> findByUserIdAndCourseIdAndStatus(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId,
            @Param("status") LessonProgressStatus status
    );

    @Query("""
  SELECT new codeverse.com.web_be.dto.response.RankingResponse.RankingDTO(
      u.id, u.username, u.avatar,
      SUM(COALESCE(lp.expGained, 0))
  )
  FROM LessonProgress lp
  JOIN lp.user u
  WHERE lp.completedAt IS NOT NULL
    AND lp.status = :passed
    AND (:start IS NULL OR lp.completedAt >= :start)
    AND (:end   IS NULL OR lp.completedAt <  :end)
  GROUP BY u.id, u.username, u.avatar
  ORDER BY SUM(COALESCE(lp.expGained, 0)) DESC
""")
    List<RankingDTO> findUserRankingByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end,
            @Param("passed") LessonProgressStatus passed,
            Pageable pageable
    );

    long countByUserIdAndStatus(Long userId, LessonProgressStatus status);
    long countByUserId(Long userId);
}