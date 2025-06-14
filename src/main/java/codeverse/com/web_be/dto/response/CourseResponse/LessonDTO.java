package codeverse.com.web_be.dto.response.CourseResponse;

import codeverse.com.web_be.enums.LessonProgressStatus;
import codeverse.com.web_be.enums.LessonType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDTO {
    private Long id;
    private String title;
    private LessonType lessonType;
    private List<QuestionDTO> questions;
    private TheoryDTO theory;
    private ExerciseDTO exercise;
    private List<TestCaseDTO> testCases;
    private List<CommentDTO> comments;
    private LessonProgressStatus status;
    private String code;
}
