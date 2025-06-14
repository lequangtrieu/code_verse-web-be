package codeverse.com.web_be.dto.response.UserResponse;

import codeverse.com.web_be.enums.InstructorStatus;
import codeverse.com.web_be.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponse {
    private Long id;
    private String username;
    private String name;
    private String avatar;
    private String bio;
    private UserRole role;
    private String qrCodeUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isVerified;
    private Boolean isDeleted;
    private InstructorStatus instructorStatus;
    private String phoneNumber;
    private String teachingCredentials;
    private String educationalBackground;
}
