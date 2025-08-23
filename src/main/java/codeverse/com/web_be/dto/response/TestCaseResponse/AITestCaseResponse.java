package codeverse.com.web_be.dto.response.TestCaseResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AITestCaseResponse {
    private List<String> input;
    private String expectedOutput;
}
