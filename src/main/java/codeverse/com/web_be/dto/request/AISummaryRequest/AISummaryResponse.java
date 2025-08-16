package codeverse.com.web_be.dto.request.AISummaryRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AISummaryResponse {
    private String lessonId;
    private String title;
    private String language;
    private String transcript;
    private Summary summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private String tl;
        private List<String> keyPoints;
        private List<String> steps;
        private List<String> glossary;
        private List<String> quiz;
        private String recommendations;
    }
}