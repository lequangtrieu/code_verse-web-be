package codeverse.com.web_be.dto.response.TestCaseResponse;

import codeverse.com.web_be.entity.TestCase;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestCaseResponse {
    private Long id;
    private String input;
    private String expectedOutput;
    private String priority;
    private Boolean isPublic;

    public static TestCaseResponse fromEntity(TestCase testCase) {
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .isPublic(testCase.isPublic())
                .priority(String.valueOf(testCase.getPriority()))
                .build();
    }
}
