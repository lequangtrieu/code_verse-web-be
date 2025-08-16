package codeverse.com.web_be.dto.request.AISummaryRequest;

import lombok.Data;

@Data
public class AISummaryRequest {
    private String lessonId;
    private String type;
    private String title;
    private String contentHtml;
    private String videoUrl;
    private String locale;
}
