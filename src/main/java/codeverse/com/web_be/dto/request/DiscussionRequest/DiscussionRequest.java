package codeverse.com.web_be.dto.request.DiscussionRequest;

import lombok.Data;

@Data
public class DiscussionRequest {
    private Long lessonId;
    private String messageText;
    private Long userId;
}
