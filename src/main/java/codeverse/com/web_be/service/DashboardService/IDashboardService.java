package codeverse.com.web_be.service.DashboardService;

import codeverse.com.web_be.dto.response.DashboardResponse.DashboardOverviewDTO;
import codeverse.com.web_be.dto.response.DashboardResponse.RevenueByTimeDTO;

import java.util.List;

public interface IDashboardService {
    DashboardOverviewDTO getOverviewStats();

    List<RevenueByTimeDTO> getRevenueByYear();

    List<RevenueByTimeDTO> getRevenueByMonth();

    List<RevenueByTimeDTO> getRevenueByQuarter();
}
