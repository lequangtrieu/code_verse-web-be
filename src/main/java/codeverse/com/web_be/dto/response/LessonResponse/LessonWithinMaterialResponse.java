package codeverse.com.web_be.dto.response.LessonResponse;

import codeverse.com.web_be.dto.response.ExerciseResponse.ExerciseWithinLessonResponse;
import codeverse.com.web_be.dto.response.TheoryResponse.TheoryWithinLessonResponse;
import codeverse.com.web_be.entity.Lesson;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonWithinMaterialResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Integer duration;

    private ExerciseWithinLessonResponse exercise;
    private TheoryWithinLessonResponse theory;

    public static LessonWithinMaterialResponse fromEntity(Lesson lesson) {
        if (lesson == null) return null;

        return LessonWithinMaterialResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .duration(lesson.getDuration())
                .exercise(ExerciseWithinLessonResponse.fromEntity(lesson.getExercise()))
                .theory(TheoryWithinLessonResponse.fromEntity(lesson.getTheory()))
                .build();
    }
}
