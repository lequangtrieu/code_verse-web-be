package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.ExerciseTask;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseTaskRepository extends JpaRepository<ExerciseTask, Long> {
    long countByExerciseId(Long exerciseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ExerciseTask t WHERE t.exercise.id IN " +
            "(SELECT e.id FROM Exercise e WHERE e.lesson.id = :lessonId)")
    void deleteTasksByLessonId(Long lessonId);
}
