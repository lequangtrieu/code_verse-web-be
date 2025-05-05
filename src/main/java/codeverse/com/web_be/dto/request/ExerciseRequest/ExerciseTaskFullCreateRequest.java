package codeverse.com.web_be.dto.request.ExerciseRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseTaskFullCreateRequest {
    private String description;
}
