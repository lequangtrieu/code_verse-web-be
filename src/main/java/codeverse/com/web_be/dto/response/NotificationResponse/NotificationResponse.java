package codeverse.com.web_be.dto.response.NotificationResponse;

import codeverse.com.web_be.entity.Notification;
import codeverse.com.web_be.entity.TestCase;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.entity.UserNotification;
import codeverse.com.web_be.enums.UserRole;
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
    LocalDateTime createdAt;

    public static NotificationResponse fromEntity(UserNotification notification) {
        if(notification == null) return null;
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getNotification().getTitle())
                .content(notification.getNotification().getContent())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
