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
    private String title;
    private Integer orderIndex;
    private String lessonType;
    private Integer expReward;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LessonResponse fromEntity(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .expReward(lesson.getExpReward())
                .lessonType(lesson.getLessonType().toString())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
