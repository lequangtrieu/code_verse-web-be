package codeverse.com.web_be.service.DashboardService;

import codeverse.com.web_be.dto.response.DashboardResponse.*;
import codeverse.com.web_be.enums.CompareType;
import codeverse.com.web_be.enums.DashboardPeriodType;

import java.util.List;

public interface IDashboardService {
    DashboardOverviewDTO getOverviewStats(DashboardPeriodType period);
    List<UserRoleStatDTO> getUserRoleStats();
    List<RevenueByMonthDTO> getRevenueByYear(int year);
    List<RevenueStatDTO> getRevenueByMonth(int year, CompareType compareType);
    List<RevenueStatDTO> getRevenueByQuarter(int year, CompareType compareType);
    List<RevenueStatDTO> getRevenueByYear();
    List<InstructorRevenueDTO> getInstructorRevenue(int year, Integer month, Integer quarter);
    List<CourseRevenueDTO> getCourseRevenueByInstructor(Long instructorId, int year, Integer month, Integer quarter);

}
