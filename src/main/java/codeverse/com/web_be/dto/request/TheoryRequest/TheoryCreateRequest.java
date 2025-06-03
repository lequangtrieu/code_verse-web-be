package codeverse.com.web_be.dto.request.TheoryRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TheoryCreateRequest {
    private Long lessonId;
    private String title = "";
    private MultipartFile contentFile;
}
