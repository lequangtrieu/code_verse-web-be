package codeverse.com.web_be.dto.response.DashboardResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardOverviewDTO {
    private long totalUsers;
    private long totalInstructors;
    private long totalCourses;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long totalEnrollments;
    private long totalWithdrawalRequests;
    private long totalReports;
}
