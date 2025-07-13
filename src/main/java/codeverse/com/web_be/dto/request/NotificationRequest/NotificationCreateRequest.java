package codeverse.com.web_be.dto.request.NotificationRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationCreateRequest {
    String senderUsername;
    String title;
    String content;
    List<String> recipientUsernames;
}
