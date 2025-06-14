package codeverse.com.web_be.dto.request.CodeRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CodeRequestDTO {
    private Long lessonId;
    private Long userId;
    private String code;
    private Float executionTime;
    private Float memoryUsage;
}
