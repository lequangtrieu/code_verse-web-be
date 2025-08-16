package codeverse.com.web_be.dto.response.DashboardResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CourseRevenueDTO {
    private Long courseId;
    private String courseTitle;
    private Long totalLearners;
    private BigDecimal totalRevenue;
    private BigDecimal platformFee;
    private BigDecimal instructorIncome;
}
