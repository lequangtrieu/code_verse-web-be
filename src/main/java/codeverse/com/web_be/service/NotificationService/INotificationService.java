package codeverse.com.web_be.service.NotificationService;

import codeverse.com.web_be.dto.response.NotificationResponse.NotificationResponse;
import codeverse.com.web_be.entity.Notification;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface INotificationService extends IGenericService<Notification, Long> {
    List<NotificationResponse> getNotificationsReceivedByUser(String username);
    void markAsRead(Long userNotificationId);
    void markAllAsRead(String username);
    Long getUnreadNotificationsCountByUser(String username);
}
