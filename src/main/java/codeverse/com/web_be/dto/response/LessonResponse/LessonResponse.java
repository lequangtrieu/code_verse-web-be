package codeverse.com.web_be.dto.response.LessonResponse;

import codeverse.com.web_be.entity.Lesson;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    private Long id;
    private String materialSection;
    private String title;
    private Integer orderIndex;
    private String theory;
    private String exercise;
    private String defaultCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LessonResponse fromEntity(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .materialSection(lesson.getMaterialSection().getTitle())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .theory(lesson.getTheory() == null ? null : lesson.getTheory().getTitle())
                .exercise(lesson.getExercise() == null ? null : lesson.getExercise().getTitle())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
