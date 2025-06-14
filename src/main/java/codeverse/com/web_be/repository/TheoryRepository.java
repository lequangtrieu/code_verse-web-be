package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.Theory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TheoryRepository extends JpaRepository<Theory, Long> {
    Optional<Theory> findByLesson(Lesson lesson);
    Theory findByLessonId(Long lessonId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Theory t WHERE t.lesson.id = :lessonId")
    void deleteByLessonId(Long lessonId);
}
