package codeverse.com.web_be.service.NotificationService;

import codeverse.com.web_be.dto.response.NotificationResponse.NotificationResponse;
import codeverse.com.web_be.entity.Notification;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.entity.UserNotification;
import codeverse.com.web_be.repository.NotificationRepository;
import codeverse.com.web_be.repository.UserNotificationRepository;
import codeverse.com.web_be.repository.UserRepository;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class NotificationServiceImpl extends GenericServiceImpl<Notification, Long> implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final FunctionHelper functionHelper;
    private final UserNotificationRepository userNotificationRepository;

    protected NotificationServiceImpl(NotificationRepository notificationRepository,
                                      FunctionHelper functionHelper,
                                      UserNotificationRepository userNotificationRepository) {
        super(notificationRepository);
        this.notificationRepository = notificationRepository;
        this.functionHelper = functionHelper;
        this.userNotificationRepository = userNotificationRepository;
    }

    @Override
    public List<NotificationResponse> getNotificationsReceivedByUser(String username) {
        User user = functionHelper.getActiveUserByUsername(username);
        List<UserNotification> userNotifications = userNotificationRepository.findByUserId(user.getId());
        return userNotifications.stream()
                .map(NotificationResponse::fromEntity)
                .sorted(Comparator.comparing(NotificationResponse::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public void markAsRead(Long userNotificationId) {
        UserNotification userNotification = userNotificationRepository
                .findById(userNotificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found for user"));

        if (!userNotification.isRead()) {
            userNotification.setRead(true);
            userNotification.setReadAt(LocalDateTime.now());
            userNotificationRepository.save(userNotification);
        }
    }

    @Override
    public void markAllAsRead(String username) {
        User user = functionHelper.getActiveUserByUsername(username);
        List<UserNotification> userNotifications = userNotificationRepository.findByUserId(user.getId());
        for(UserNotification userNotification : userNotifications) {
            if(!userNotification.isRead()) {
                userNotification.setRead(true);
                userNotification.setReadAt(LocalDateTime.now());
                userNotificationRepository.save(userNotification);
            }
        }
    }

    @Override
    public Long getUnreadNotificationsCountByUser(String username) {
        User user = functionHelper.getActiveUserByUsername(username);
        return userNotificationRepository.countByIsReadFalseAndUserId(user.getId());
    }
}
