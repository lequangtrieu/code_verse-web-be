package codeverse.com.web_be.dto.response.UserResponse;

import codeverse.com.web_be.enums.BadgeType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    List<BadgeType> badges = new ArrayList<>();
    LocalDateTime createdAt;
}
