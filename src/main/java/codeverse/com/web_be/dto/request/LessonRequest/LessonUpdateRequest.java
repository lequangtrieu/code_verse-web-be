package codeverse.com.web_be.dto.request.LessonRequest;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseFullCreateRequest;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseUpdateRequest;
import codeverse.com.web_be.dto.request.TheoryRequest.TheoryFullCreateRequest;
import codeverse.com.web_be.dto.request.TheoryRequest.TheoryUpdateRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateRequest {
    private Long id;
    private String title = "";
    private Integer orderIndex;
    private Integer duration;

    private TheoryUpdateRequest theory;
    private ExerciseUpdateRequest exercise;
}
