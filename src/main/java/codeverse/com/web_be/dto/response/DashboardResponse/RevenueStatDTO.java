package codeverse.com.web_be.dto.response.DashboardResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueStatDTO {
    private String label;          // Tháng/Quý/Năm
    private long totalOrders;      // số đơn hàng
    private BigDecimal totalRevenue; // doanh thu
    private double growthPercent;  // % tăng trưởng so với kỳ trước
}

