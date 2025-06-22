package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.QuizAnswer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM QuizAnswer qa WHERE qa.question.id IN :questionIds")
    void deleteByQuestionIdIn(@Param("questionIds") List<Long> questionIds);

    long countByQuestionIdAndIsCorrectTrue(Long questionId);

    List<QuizAnswer> findByQuestionId(Long questionId);
}
