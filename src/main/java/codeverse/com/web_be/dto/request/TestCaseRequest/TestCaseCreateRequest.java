package codeverse.com.web_be.dto.request.TestCaseRequest;

import codeverse.com.web_be.enums.TestCasePriority;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestCaseCreateRequest {
    private Long exerciseId;
    private String input;
    private String expectedOutput;
    private TestCasePriority priority;
    private boolean isPublic;
}
