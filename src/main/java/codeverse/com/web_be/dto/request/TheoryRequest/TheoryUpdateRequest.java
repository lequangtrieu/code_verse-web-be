package codeverse.com.web_be.dto.request.TheoryRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TheoryUpdateRequest {
    private Long id;
    private String title = "";
    private String content;
}
