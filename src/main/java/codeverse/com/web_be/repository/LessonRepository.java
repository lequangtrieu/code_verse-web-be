package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
