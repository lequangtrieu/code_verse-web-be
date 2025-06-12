package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByVerificationToken(String verificationToken);
    @Query("SELECT u FROM User u WHERE u.role = 'INSTRUCTOR' AND u.isActiveInstructor = false")
    List<User> findInactiveInstructors();
}

