package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Exercise;
import codeverse.com.web_be.entity.Lesson;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByLesson(Lesson lesson);
    Exercise findByLessonId(Long lessonId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Exercise t WHERE t.lesson.id = :lessonId")
    void deleteByLessonId(Long lessonId);
}
