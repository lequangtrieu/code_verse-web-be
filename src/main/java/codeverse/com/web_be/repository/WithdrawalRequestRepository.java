package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.WithdrawalRequest;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {

    List<WithdrawalRequest> findByInstructor(User instructor);

    boolean existsByInstructorAndStatusIn(User instructor, List<WithdrawalStatus> statuses);

    Optional<WithdrawalRequest> findByVerifyToken(String token);
}
