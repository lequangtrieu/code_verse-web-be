package codeverse.com.web_be.dto.request.ReportReasonRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportReasonRequest {
    private String title;
    private String description;
}
