package codeverse.com.web_be.dto.response.TheoryResponse;

import codeverse.com.web_be.entity.Theory;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TheoryWithinLessonResponse {
    private Long id;
    private String title;
    private String content;

    public static TheoryWithinLessonResponse fromEntity(Theory theory) {
        if (theory == null) return null;

        return TheoryWithinLessonResponse.builder()
                .id(theory.getId())
                .title(theory.getTitle())
                .content(theory.getContent())
                .build();
    }
}
