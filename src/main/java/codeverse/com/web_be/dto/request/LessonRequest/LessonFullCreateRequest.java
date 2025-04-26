package codeverse.com.web_be.dto.request.LessonRequest;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseFullCreateRequest;
import codeverse.com.web_be.dto.request.TheoryRequest.TheoryFullCreateRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonFullCreateRequest {
    private String title;

    private TheoryFullCreateRequest theory;
    private ExerciseFullCreateRequest exercise;
}
