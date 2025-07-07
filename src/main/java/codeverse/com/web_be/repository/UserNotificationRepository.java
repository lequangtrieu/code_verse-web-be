package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByUserId(Long userId);
    Optional<UserNotification> findByUserIdAndNotificationId(Long userId, Long notificationId);
    long countByIsReadFalseAndUserId(Long userId);

}
