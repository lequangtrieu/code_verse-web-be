package codeverse.com.web_be.dto.response.ExerciseResponse;

import codeverse.com.web_be.entity.Exercise;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseResponse {
    private Long id;
    private String lesson;
    private String title;
    private Integer expReward;
    private String instruction;
    private List<ExerciseTaskResponse> tasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExerciseResponse fromEntity(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .lesson(exercise.getLesson().getTitle())
                .title(exercise.getTitle())
                .expReward(exercise.getExpReward())
                .instruction(exercise.getInstruction())
                .tasks(exercise.getTasks() == null ? null :
                        exercise.getTasks().stream()
                                .map(ExerciseTaskResponse::fromEntity)
                                .toList())
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .build();
    }
}
