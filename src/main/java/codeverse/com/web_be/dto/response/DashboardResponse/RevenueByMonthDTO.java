package codeverse.com.web_be.dto.response.DashboardResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByMonthDTO {
    private int month;
    private BigDecimal totalRevenue;
}
