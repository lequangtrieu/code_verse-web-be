package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, Long> {
    @Query("SELECT COUNT(cs) FROM CodeSubmission cs " +
            "JOIN cs.lessonProgress lp " +
            "WHERE lp.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}