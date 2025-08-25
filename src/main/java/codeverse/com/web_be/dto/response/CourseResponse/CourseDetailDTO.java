package codeverse.com.web_be.dto.response.CourseResponse;

import codeverse.com.web_be.entity.LessonProgress;
import codeverse.com.web_be.enums.CodeLanguage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDetailDTO {
    private List<CourseModuleDTO> data;
    private CodeLanguage language;
    private List<LessonProgress> lessonProgresses;
    private String instructor;
}
