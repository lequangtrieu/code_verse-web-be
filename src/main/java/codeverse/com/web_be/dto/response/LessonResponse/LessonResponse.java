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
    private String courseModule;
    private String title;
    private Integer orderIndex;
    private String theory;
    private String exercise;
    private String lessonType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LessonResponse fromEntity(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .courseModule(lesson.getCourseModule().getTitle())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .theory(lesson.getTheory() == null ? null : lesson.getTheory().getTitle())
                .exercise(lesson.getExercise() == null ? null : lesson.getExercise().getTitle())
                .lessonType(lesson.getLessonType().toString())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
