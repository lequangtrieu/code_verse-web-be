package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.TestCase;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    long countByExerciseId(Long exerciseId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TestCase t WHERE t.exercise.id IN " +
            "(SELECT e.id FROM Exercise e WHERE e.lesson.id = :lessonId)")
    void deleteTestCasesByLessonId(Long lessonId);

} 