package codeverse.com.web_be.dto.response.CourseResponse;

import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleForUpdateResponse;
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
    private String language;
    private String category;
    private BigDecimal discount;
    private Long categoryId;
    private String instructor;
    private Long instructorId;
    private BigDecimal price;
    private String status;
    private boolean deleted;

    private List<CourseModuleForUpdateResponse> modules;
}
