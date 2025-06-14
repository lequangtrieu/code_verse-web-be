package codeverse.com.web_be.dto.response.CourseResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyDTO {
    private String id;
    private String author;
    private String content;
    private String createdAt;
}