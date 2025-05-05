package codeverse.com.web_be.dto.response.ExerciseResponse;

import codeverse.com.web_be.entity.ExerciseTask;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseTaskWithinExerciseResponse {
    Long id;
    String description;

    public static ExerciseTaskWithinExerciseResponse fromEntity(ExerciseTask task) {
        if (task == null) return null;

        return ExerciseTaskWithinExerciseResponse.builder()
                .id(task.getId())
                .description(task.getDescription())
                .build();
    }
}
