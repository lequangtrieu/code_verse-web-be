package codeverse.com.web_be.dto.response.CourseResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseModuleDTO {
    private Long id;
    private String title;
    private List<LessonDTO> subLessons;
}