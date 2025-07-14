package codeverse.com.web_be.dto.response.DashboardResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByTimeDTO {
    private String label;
    private BigDecimal total;

    public RevenueByTimeDTO(Integer year, BigDecimal total) {
        this.label = String.valueOf(year);
        this.total = total;
    }
}
