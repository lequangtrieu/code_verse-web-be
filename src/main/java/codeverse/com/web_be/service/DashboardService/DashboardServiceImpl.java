package codeverse.com.web_be.service.DashboardService;

import codeverse.com.web_be.dto.response.DashboardResponse.DashboardOverviewDTO;
import codeverse.com.web_be.dto.response.DashboardResponse.RevenueByTimeDTO;
import codeverse.com.web_be.enums.InstructorStatus;
import codeverse.com.web_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final UserReportRepository userReportRepository;

    @Override
    public DashboardOverviewDTO getOverviewStats() {
        long totalUsers = userRepository.count();
        long totalInstructors = userRepository.countByInstructorStatus(InstructorStatus.APPROVED);
        long totalCourses = courseRepository.count();
        long totalOrders = orderRepository.count();
        BigDecimal totalRevenue = orderRepository.sumTotalAmount();
        long totalEnrollments = enrollmentRepository.count();
        long totalReports = userReportRepository.count();

        return new DashboardOverviewDTO(
                totalUsers,
                totalInstructors,
                totalCourses,
                totalOrders,
                totalRevenue,
                totalEnrollments,
                0L,
                totalReports
        );
    }

    @Override
    public List<RevenueByTimeDTO> getRevenueByYear() {
        List<Object[]> raw = orderRepository.getRevenueByYearRaw();
        List<RevenueByTimeDTO> result = new ArrayList<>();
        for (Object[] row : raw) {
            String year = String.valueOf(row[0]);
            BigDecimal total = (BigDecimal) row[1];
            result.add(new RevenueByTimeDTO(year, total));
        }
        return result;
    }

    @Override
    public List<RevenueByTimeDTO> getRevenueByMonth() {
        List<Object[]> raw = orderRepository.getRevenueByMonthRaw();
        List<RevenueByTimeDTO> result = new ArrayList<>();
        for (Object[] row : raw) {
            int month = (int) row[0];
            int year = (int) row[1];
            BigDecimal total = (BigDecimal) row[2];
            result.add(new RevenueByTimeDTO(month + "/" + year, total));
        }
        return result;
    }

    @Override
    public List<RevenueByTimeDTO> getRevenueByQuarter() {
        List<Object[]> raw = orderRepository.getRevenueByQuarterRaw();
        List<RevenueByTimeDTO> result = new ArrayList<>();
        for (Object[] row : raw) {
            int quarter = ((Number) row[0]).intValue();
            int year = (int) row[1];
            BigDecimal total = (BigDecimal) row[2];
            result.add(new RevenueByTimeDTO("Q" + quarter + "-" + year, total));
        }
        return result;
    }
}