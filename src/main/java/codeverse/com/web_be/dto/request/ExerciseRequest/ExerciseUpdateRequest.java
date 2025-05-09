package codeverse.com.web_be.dto.request.ExerciseRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseUpdateRequest {
    private Long id;
    private String title = "";
    private String instruction;
    private Integer expReward;

    private List<ExerciseTaskUpdateRequest> tasks;
}
