package codeverse.com.web_be.dto.request.CodeExecutionRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CodeExecutionRequest {
    private String language;
    private String code;
    private String input;
}
