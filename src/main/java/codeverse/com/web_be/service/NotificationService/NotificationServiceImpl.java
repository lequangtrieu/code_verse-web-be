package codeverse.com.web_be.service.NotificationService;

import codeverse.com.web_be.dto.request.NotificationRequest.NotificationCreateRequest;
import codeverse.com.web_be.dto.response.NotificationResponse.NotificationResponse;
import codeverse.com.web_be.entity.Notification;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.entity.UserNotification;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.mapper.UserMapper;
import codeverse.com.web_be.repository.NotificationRepository;
import codeverse.com.web_be.repository.UserNotificationRepository;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class NotificationServiceImpl extends GenericServiceImpl<Notification, Long> implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final FunctionHelper functionHelper;
    private final UserNotificationRepository userNotificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserMapper userMapper;

    protected NotificationServiceImpl(NotificationRepository notificationRepository,
                                      FunctionHelper functionHelper,
                                      UserNotificationRepository userNotificationRepository,
                                      SimpMessagingTemplate messagingTemplate,
                                      UserMapper userMapper) {
        super(notificationRepository);
        this.notificationRepository = notificationRepository;
        this.functionHelper = functionHelper;
        this.userNotificationRepository = userNotificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public List<NotificationResponse> getNotificationsReceivedByUser(String username) {
        User user = functionHelper.getActiveUserByUsername(username);
        List<UserNotification> userNotifications = userNotificationRepository.findByUserId(user.getId());

        return userNotifications.stream()
                .map(un -> NotificationResponse.fromEntity(un, userMapper.userToUserResponse(un.getNotification().getCreatedBy())))
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
        for (UserNotification userNotification : userNotifications) {
            if (!userNotification.isRead()) {
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

    @Override
    public void notifyUsers(List<User> recipients, User sender, String title, String content) {
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .createdBy(sender)
                .build();
        notificationRepository.save(notification);

        for (User recipient : recipients) {
            UserNotification un = userNotificationRepository.save(UserNotification.builder()
                    .user(recipient)
                    .notification(notification)
                    .build());

            NotificationResponse response = NotificationResponse.fromEntity(un, userMapper.userToUserResponse(un.getNotification().getCreatedBy()));
            if(response.getTitle().startsWith("_ADMIN_")) response.setTitle(response.getTitle().substring(7));
            messagingTemplate.convertAndSendToUser(
                    recipient.getUsername(),
                    "/queue/notifications",
                    response
            );
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        User sender = functionHelper.getActiveUserByUsername(request.getSenderUsername());
        List<User> recipients = new ArrayList<>();
        for(String recUsn : request.getRecipientUsernames()){
            User recipient = functionHelper.getActiveUserByUsername(recUsn);
            recipients.add(recipient);
        }
        notifyUsers(recipients, sender, request.getTitle(), request.getContent());
        return NotificationResponse.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<NotificationResponse> getAllNotificationsSentByAdmin() {
        List<Notification> notifications = notificationRepository.findByCreatedBy_Role(UserRole.ADMIN);
        return notifications.stream()
                .filter(notification -> notification.getTitle().startsWith("_ADMIN_"))
                .map(n -> NotificationResponse.builder()
                        .title(n.getTitle().substring(7))
                        .content(n.getContent())
                        .sender(userMapper.userToUserResponse(n.getCreatedBy()))
                        .createdAt(n.getCreatedAt())
                        .build())
                .sorted(Comparator.comparing(NotificationResponse::getCreatedAt).reversed())
                .toList();
    }
}
