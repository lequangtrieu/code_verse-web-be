package codeverse.com.web_be.dto.response.CourseRatingResponse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewItem {
    private String username;
    private String userAvatar;
    private float rating;
    private String comment;
    private LocalDateTime createdAt;
}