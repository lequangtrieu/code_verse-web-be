package codeverse.com.web_be.dto.request.AuthenRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignUpRequest {
    String username;
    String name;
    String password;
    String phoneNumber;
    MultipartFile teachingCredentials;
    String educationalBackground;
    String role;
}