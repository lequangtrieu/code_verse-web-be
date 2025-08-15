package codeverse.com.web_be.dto.response.DashboardResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InstructorRevenueDTO {
    private Long instructorId;
    private String instructorName;
    private Long totalCourses;
    private Long totalLearners;
    private BigDecimal totalRevenue;
    private BigDecimal platformFee;
    private BigDecimal instructorIncome;
    private Long withdrawalsCount;
    private BigDecimal totalWithdrawn;
    private BigDecimal pendingWithdrawals;
}
