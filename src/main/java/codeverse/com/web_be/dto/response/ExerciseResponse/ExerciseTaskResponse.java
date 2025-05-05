package codeverse.com.web_be.dto.response.ExerciseResponse;

import codeverse.com.web_be.entity.ExerciseTask;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseTaskResponse {
    private Long id;
    private String exercise;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExerciseTaskResponse fromEntity(ExerciseTask exerciseTask) {
        return ExerciseTaskResponse.builder()
                .id(exerciseTask.getId())
                .exercise(exerciseTask.getExercise().getTitle())
                .description(exerciseTask.getDescription())
                .createdAt(exerciseTask.getCreatedAt())
                .updatedAt(exerciseTask.getUpdatedAt())
                .build();
    }
}
