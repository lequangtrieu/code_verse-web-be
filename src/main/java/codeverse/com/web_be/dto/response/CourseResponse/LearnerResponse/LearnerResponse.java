package codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse;

import codeverse.com.web_be.dto.response.UserResponse.UserResponse;
import codeverse.com.web_be.entity.CourseEnrollment;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearnerResponse {
    UserResponse learner;
    Float completionPercentage;
    Integer totalExpGained;
    LocalDateTime completedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static LearnerResponse fromEntity(CourseEnrollment enrollment) {
        if(enrollment == null) return null;
        return LearnerResponse.builder()
                .completionPercentage(enrollment.getCompletionPercentage())
                .totalExpGained(enrollment.getTotalExpGained())
                .completedAt(enrollment.getCompletedAt())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}
