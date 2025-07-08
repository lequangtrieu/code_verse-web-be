package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
