package codeverse.com.web_be.dto.response.ReportReasonResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportReasonResponse {
    private Long id;
    private String title;
    private String description;
    private Boolean active;
}
