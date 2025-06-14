package codeverse.com.web_be.dto.response.CourseResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestCaseDTO {
    private Long id;
    private String input;
    private String expected;
}