package codeverse.com.web_be.dto.response.ExerciseResponse;

import codeverse.com.web_be.entity.Exercise;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseWithinLessonResponse {
    private Long id;
    private String title;
    private String instruction;
    private Integer expReward;

    private List<ExerciseTaskWithinExerciseResponse> tasks;

    public static ExerciseWithinLessonResponse fromEntity(Exercise exercise) {
        if (exercise == null) return null;

        return ExerciseWithinLessonResponse.builder()
                .id(exercise.getId())
                .title(exercise.getTitle())
                .instruction(exercise.getInstruction())
                .expReward(exercise.getExpReward())
                .tasks(exercise.getTasks() == null ? null :
                        exercise.getTasks().stream()
                                .map(ExerciseTaskWithinExerciseResponse::fromEntity)
                                .toList()
                )
                .build();
    }
}
