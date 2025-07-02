package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.LessonProgressDTO.LessonProgressDTO;
import codeverse.com.web_be.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
} 