package codeverse.com.web_be.dto.response.CourseResponse;

import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionForUpdateResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CourseForUpdateResponse {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String level;
    private String category;
    private String instructor;
    private BigDecimal price;
    private boolean published;
    private boolean deleted;

    private List<MaterialSectionForUpdateResponse> modules;
}
