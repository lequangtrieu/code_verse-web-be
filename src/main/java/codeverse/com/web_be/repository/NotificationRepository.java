package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Notification;
import codeverse.com.web_be.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCreatedBy_Role(UserRole createdBy_role);
}
