package codeverse.com.web_be.dto.response.UserResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String username;
    String role;
    String name;
    String avatar;
    String bio;
    Boolean isDeleted;
    String qrCodeUrl;
    String phoneNumber;
    String teachingCredentials;
    String educationalBackground;
    LocalDateTime createdAt;
}
