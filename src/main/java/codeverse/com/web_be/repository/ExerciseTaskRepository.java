package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.ExerciseTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseTaskRepository extends JpaRepository<ExerciseTask, Long> {
}
