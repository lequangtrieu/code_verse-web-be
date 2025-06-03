package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.Theory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TheoryRepository extends JpaRepository<Theory, Long> {
    Optional<Theory> findByLesson(Lesson lesson);
}
