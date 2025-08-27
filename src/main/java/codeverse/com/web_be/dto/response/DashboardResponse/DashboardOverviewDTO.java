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
    private long newUsers;
    private double userGrowthPercent;

    private long totalCourses;
    private long newCourses;
    private double courseGrowthPercent;

    private long totalOrders;
    private long newOrders;
    private double orderGrowthPercent;

    private BigDecimal totalRevenue;
    private BigDecimal newRevenue;
    private double revenueGrowthPercent;
}
