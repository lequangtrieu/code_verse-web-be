package codeverse.com.web_be.dto.response.NotificationResponse;

import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.UserNotification;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;
    String title;
    String content;
    Boolean read;
    UserResponse sender;
    LocalDateTime createdAt;

    public static NotificationResponse fromEntity(UserNotification notification, UserResponse sender) {
        if(notification == null) return null;
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getNotification().getTitle().startsWith("_ADMIN_") ?
                        notification.getNotification().getTitle().substring(7) :
                        notification.getNotification().getTitle())
                .content(notification.getNotification().getContent())
                .read(notification.isRead())
                .sender(sender)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
