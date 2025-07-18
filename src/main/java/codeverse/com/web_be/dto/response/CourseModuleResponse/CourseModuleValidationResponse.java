package codeverse.com.web_be.dto.response.CourseModuleResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseModuleValidationResponse {
    private boolean valid;
    private List<String> errors;
}
