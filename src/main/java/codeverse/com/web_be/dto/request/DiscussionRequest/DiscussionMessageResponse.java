package codeverse.com.web_be.dto.request.DiscussionRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscussionMessageResponse {
    private Long id;
    private String messageText;
    private String authorEmail;
    private String avatar;
    private LocalDateTime createdAt;
    private List<DiscussionMessageResponse> replies;
    private Long userId;
    private String originalMessage;
    private Boolean isDeleted;
}