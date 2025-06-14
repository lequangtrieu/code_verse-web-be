package codeverse.com.web_be.dto.response.TheoryResponse;

import codeverse.com.web_be.entity.Theory;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TheoryResponse {
    private Long id;
    private String lesson;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TheoryResponse fromEntity(Theory theory) {
        return TheoryResponse.builder()
                .id(theory.getId())
                .lesson(theory.getLesson().getTitle())
                .title(theory.getTitle())
                .content(theory.getContent())
                .createdAt(theory.getCreatedAt())
                .updatedAt(theory.getUpdatedAt())
                .build();
    }
}
