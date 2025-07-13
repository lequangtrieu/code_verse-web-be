package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.WithdrawalRequest;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {

    List<WithdrawalRequest> findByInstructor(User instructor);

    boolean existsByInstructorAndStatusIn(User instructor, List<WithdrawalStatus> statuses);

    Optional<WithdrawalRequest> findByVerifyToken(String token);

    @Query("SELECT wr FROM WithdrawalRequest wr " +
            "JOIN FETCH wr.instructor u " +
            "WHERE wr.status <> codeverse.com.web_be.enums.WithdrawalStatus.CANCELLED " +
            "AND (:status IS NULL OR wr.status = :status) " +
            "AND (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:startDate IS NULL OR wr.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR wr.createdAt <= :endDate)")
    List<WithdrawalRequest> filterAll(
            @Param("status") WithdrawalStatus status,
            @Param("name") String name,
            @Param("startDate") LocalDateTime start,
            @Param("endDate") LocalDateTime end
    );
}
