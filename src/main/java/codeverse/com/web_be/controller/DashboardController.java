package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.DashboardResponse.DashboardOverviewDTO;
import codeverse.com.web_be.dto.response.DashboardResponse.RevenueByTimeDTO;
import codeverse.com.web_be.service.DashboardService.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDTO> getOverview() {
        return ResponseEntity.ok(dashboardService.getOverviewStats());
    }

    @GetMapping("/revenue/yearly")
    public ResponseEntity<List<RevenueByTimeDTO>> getYearlyRevenue() {
        return ResponseEntity.ok(dashboardService.getRevenueByYear());
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<RevenueByTimeDTO>> getMonthlyRevenue() {
        return ResponseEntity.ok(dashboardService.getRevenueByMonth());
    }

    @GetMapping("/revenue/quarterly")
    public ResponseEntity<List<RevenueByTimeDTO>> getQuarterlyRevenue() {
        return ResponseEntity.ok(dashboardService.getRevenueByQuarter());
    }
}