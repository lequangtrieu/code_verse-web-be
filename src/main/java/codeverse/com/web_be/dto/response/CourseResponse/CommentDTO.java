package codeverse.com.web_be.dto.response.CourseResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDTO {
    private String id;
    private String author;
    private String content;
    private String createdAt;
    private List<ReplyDTO> replies;
}
