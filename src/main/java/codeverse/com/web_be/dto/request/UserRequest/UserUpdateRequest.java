package codeverse.com.web_be.dto.request.UserRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String username;
    String name;
    MultipartFile avatar;
    String bio;
    MultipartFile qrCodeUrl;
    String phoneNumber;
    String teachingCredentials;
    String educationalBackground;
}
