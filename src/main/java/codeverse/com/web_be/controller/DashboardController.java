package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.DashboardResponse.*;
import codeverse.com.web_be.enums.CompareType;
import codeverse.com.web_be.enums.DashboardPeriodType;
import codeverse.com.web_be.enums.PeriodType;
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
    public DashboardOverviewDTO getOverview(@RequestParam(defaultValue = "WEEK") DashboardPeriodType period) {
        return dashboardService.getOverviewStats(period);
    }

    @GetMapping("/revenue/month")
    public List<RevenueStatDTO> getRevenueByMonth(
            @RequestParam int year,
            @RequestParam(defaultValue = "PREVIOUS_PERIOD") CompareType compareType) {
        return dashboardService.getRevenueByMonth(year,compareType);
    }

    @GetMapping("/revenue/quarter")
    public List<RevenueStatDTO> getRevenueByQuarter(
            @RequestParam int year,
            @RequestParam(defaultValue = "PREVIOUS_PERIOD") CompareType compareType) {
        return dashboardService.getRevenueByQuarter(year, compareType);
    }

    @GetMapping("/revenue/year")
    public List<RevenueStatDTO> getRevenueByYear() {
        return dashboardService.getRevenueByYear();
    }

    @GetMapping("/user-role-stats")
    public ResponseEntity<List<UserRoleStatDTO>> getUserRoleStats() {
        return ResponseEntity.ok(dashboardService.getUserRoleStats());
    }

    @GetMapping("/revenue-by-year")
    public List<RevenueByMonthDTO> getRevenueByYear(@RequestParam int year) {
        return dashboardService.getRevenueByYear(year);
    }

    @GetMapping("/revenue/instructors")
    public ResponseEntity<List<InstructorRevenueDTO>> getInstructorRevenue(
            @RequestParam int year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer quarter
    ) {
        return ResponseEntity.ok(dashboardService.getInstructorRevenue(year, month, quarter));
    }

    @GetMapping("/revenue/instructors/{instructorId}/courses")
    public ResponseEntity<List<CourseRevenueDTO>> getCourseRevenueByInstructor(
            @PathVariable Long instructorId,
            @RequestParam int year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer quarter
    ) {
        return ResponseEntity.ok(
                dashboardService.getCourseRevenueByInstructor(instructorId, year, month, quarter)
        );
    }

}