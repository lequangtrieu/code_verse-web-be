package codeverse.com.web_be.service.DashboardService;

import codeverse.com.web_be.dto.response.DashboardResponse.*;
import codeverse.com.web_be.enums.CompareType;
import codeverse.com.web_be.enums.DashboardPeriodType;
import codeverse.com.web_be.enums.PeriodType;
import codeverse.com.web_be.enums.UserRole;
import codeverse.com.web_be.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final UserReportRepository userReportRepository;

    @Override
    public DashboardOverviewDTO getOverviewStats(DashboardPeriodType period) {
        LocalDateTime now = LocalDateTime.now();

        // Kỳ hiện tại
        LocalDateTime currentStart = getStartDate(period, now);
        LocalDateTime currentEnd = getEndDate(period, now);

        // Kỳ trước
        LocalDateTime prevStart = getPrevStartDate(period, now);
        LocalDateTime prevEnd = getPrevEndDate(period, now);

        // ===== Users =====
        long totalUsers = userRepository.countActiveUsers();
        long newUsersThisPeriod = userRepository.countNewUsers(currentStart, currentEnd);
        long newUsersPrevPeriod = userRepository.countNewUsers(prevStart, prevEnd);
        double userGrowthPercent = calculateGrowthPercent(newUsersThisPeriod, newUsersPrevPeriod);

        // ===== Courses =====
        long totalCourses = courseRepository.count();
        long newCoursesThisPeriod = courseRepository.countNewCourses(currentStart, currentEnd);
        long newCoursesPrevPeriod = courseRepository.countNewCourses(prevStart, prevEnd);
        double courseGrowthPercent = calculateGrowthPercent(newCoursesThisPeriod, newCoursesPrevPeriod);

        // ===== Orders =====
        long totalOrders = orderRepository.count();
        long newOrdersThisPeriod = orderRepository.countNewOrders(currentStart, currentEnd);
        long newOrdersPrevPeriod = orderRepository.countNewOrders(prevStart, prevEnd);
        double orderGrowthPercent = calculateGrowthPercent(newOrdersThisPeriod, newOrdersPrevPeriod);

        // ===== Revenue =====
        BigDecimal totalRevenue = orderRepository.sumTotalAmount();
        BigDecimal newRevenueThisPeriod = orderRepository.sumNewRevenue(currentStart, currentEnd);
        BigDecimal newRevenuePrevPeriod = orderRepository.sumNewRevenue(prevStart, prevEnd);
        double revenueGrowthPercent = calculateGrowthPercent(newRevenueThisPeriod, newRevenuePrevPeriod);

        return new DashboardOverviewDTO(
                totalUsers, newUsersThisPeriod, userGrowthPercent,
                totalCourses, newCoursesThisPeriod, courseGrowthPercent,
                totalOrders, newOrdersThisPeriod, orderGrowthPercent,
                totalRevenue, newRevenueThisPeriod, revenueGrowthPercent
        );
    }

    private double calculateGrowthPercent(long current, long previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((double) (current - previous) / previous) * 100;
    }

    private double calculateGrowthPercent(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0)
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .doubleValue() * 100;
    }

    private LocalDateTime getStartDate(DashboardPeriodType period, LocalDateTime now) {
        switch (period) {
            case WEEK:
                return now.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
            case MONTH:
                return now.withDayOfMonth(1).with(LocalTime.MIN);
            case QUARTER:
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int startMonth = (currentQuarter - 1) * 3 + 1;
                return LocalDateTime.of(LocalDate.of(now.getYear(), startMonth, 1), LocalTime.MIN);
            case YEAR:
                return LocalDateTime.of(now.getYear(), 1, 1, 0, 0);
            default:
                return now.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        }
    }

    private LocalDateTime getEndDate(DashboardPeriodType period, LocalDateTime now) {
        switch (period) {
            case WEEK:
                return now.with(DayOfWeek.SUNDAY).with(LocalTime.MAX);
            case MONTH:
                YearMonth thisMonth = YearMonth.of(now.getYear(), now.getMonth());
                return LocalDateTime.of(thisMonth.getYear(), thisMonth.getMonth(),
                        thisMonth.lengthOfMonth(), 23, 59, 59);
            case QUARTER:
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int endMonth = currentQuarter * 3;
                int lastDay = YearMonth.of(now.getYear(), endMonth).lengthOfMonth();
                return LocalDateTime.of(now.getYear(), endMonth, lastDay, 23, 59, 59);
            case YEAR:
                return LocalDateTime.of(now.getYear(), 12, 31, 23, 59, 59);
            default:
                return now;
        }
    }

    private LocalDateTime getPrevStartDate(DashboardPeriodType period, LocalDateTime now) {
        switch (period) {
            case WEEK:
                return now.minusWeeks(1).with(DayOfWeek.MONDAY).with(LocalTime.MIN);
            case MONTH:
                return now.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
            case QUARTER:
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int prevQuarter = currentQuarter - 1 <= 0 ? 4 : currentQuarter - 1;
                int startMonth = (prevQuarter - 1) * 3 + 1;
                int year = now.getYear() - (prevQuarter == 4 ? 1 : 0);
                return LocalDateTime.of(LocalDate.of(year, startMonth, 1), LocalTime.MIN);
            case YEAR:
                return LocalDateTime.of(now.getYear() - 1, 1, 1, 0, 0);
            default:
                return now.minusWeeks(1).with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        }
    }

    private LocalDateTime getPrevEndDate(DashboardPeriodType period, LocalDateTime now) {
        switch (period) {
            case WEEK:
                return now.minusWeeks(1).with(DayOfWeek.SUNDAY).with(LocalTime.MAX);
            case MONTH:
                YearMonth prevMonth = YearMonth.of(now.minusMonths(1).getYear(), now.minusMonths(1).getMonth());
                return LocalDateTime.of(prevMonth.getYear(), prevMonth.getMonth(),
                        prevMonth.lengthOfMonth(), 23, 59, 59);
            case QUARTER:
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int prevQuarter = currentQuarter - 1 <= 0 ? 4 : currentQuarter - 1;
                int endMonth = prevQuarter * 3;
                int year = now.getYear() - (prevQuarter == 4 ? 1 : 0);
                int lastDay = YearMonth.of(year, endMonth).lengthOfMonth();
                return LocalDateTime.of(year, endMonth, lastDay, 23, 59, 59);
            case YEAR:
                return LocalDateTime.of(now.getYear() - 1, 12, 31, 23, 59, 59);
            default:
                return now.minusWeeks(1).with(DayOfWeek.SUNDAY).with(LocalTime.MAX);
        }
    }

    @Override
    public List<UserRoleStatDTO> getUserRoleStats() {
        List<Object[]> results = userRepository.countUsersByRole();
        return results.stream()
                .map(obj -> new UserRoleStatDTO((UserRole) obj[0], (Long) obj[1]))
                .toList();
    }

    public List<RevenueByMonthDTO> getRevenueByYear(int year) {
        List<Object[]> results = orderRepository.sumRevenueByMonth(year);
        List<RevenueByMonthDTO> dtos = new ArrayList<>();

        // đảm bảo đủ 12 tháng (các tháng không có doanh thu = 0)
        for (int i = 1; i <= 12; i++) {
            BigDecimal revenue = BigDecimal.ZERO;
            for (Object[] row : results) {
                int month = ((Number) row[0]).intValue();
                if (month == i) {
                    revenue = (BigDecimal) row[1];
                    break;
                }
            }
            dtos.add(new RevenueByMonthDTO(i, revenue));
        }
        return dtos;
    }

    @Override
    public List<RevenueStatDTO> getRevenueByMonth(int year, CompareType compareType) {
        List<Object[]> current = orderRepository.revenueByMonth(year);
        List<RevenueStatDTO> stats = new ArrayList<>();

        if (compareType == CompareType.PREVIOUS_PERIOD) {
            BigDecimal prevRevenue = null;
            for (Object[] row : current) {
                int month = ((Number) row[0]).intValue();
                long orders = ((Number) row[1]).longValue();
                BigDecimal revenue = (BigDecimal) row[2];

                double growth = 0.0;
                if (prevRevenue != null && prevRevenue.compareTo(BigDecimal.ZERO) > 0) {
                    growth = revenue.subtract(prevRevenue)
                            .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }
                
                String label = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                stats.add(new RevenueStatDTO(label, orders, revenue, growth));
                prevRevenue = revenue;
            }
        } else if (compareType == CompareType.SAME_PERIOD_LAST_YEAR) {
            List<Object[]> prev = orderRepository.revenueByMonth(year - 1);
            Map<Integer, BigDecimal> prevMap = new HashMap<>();
            for (Object[] row : prev) {
                int month = ((Number) row[0]).intValue();
                BigDecimal revenue = (BigDecimal) row[2];
                prevMap.put(month, revenue);
            }

            for (Object[] row : current) {
                int month = ((Number) row[0]).intValue();
                long orders = ((Number) row[1]).longValue();
                BigDecimal revenue = (BigDecimal) row[2];

                BigDecimal prevRevenue = prevMap.getOrDefault(month, BigDecimal.ZERO);
                double growth = 0.0;
                if (prevRevenue.compareTo(BigDecimal.ZERO) == 0) {
                    if (revenue.compareTo(BigDecimal.ZERO) > 0) {
                        growth = 100.0;
                    } else {
                        growth = 0.0;
                    }
                } else {
                    growth = revenue.subtract(prevRevenue)
                            .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }

                // English month label
                String label = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                stats.add(new RevenueStatDTO(label, orders, revenue, growth));
            }
        }

        return stats;
    }

    @Override
    public List<RevenueStatDTO> getRevenueByQuarter(int year, CompareType compareType) {
        List<Object[]> current = orderRepository.revenueByQuarter(year);
        List<RevenueStatDTO> stats = new ArrayList<>();

        if (compareType == CompareType.PREVIOUS_PERIOD) {
            // So với quý liền trước trong cùng năm
            BigDecimal prevRevenue = null;
            for (Object[] row : current) {
                int quarter = ((Number) row[0]).intValue();
                long orders = ((Number) row[1]).longValue();
                BigDecimal revenue = (BigDecimal) row[2];

                double growth = 0.0;
                if (prevRevenue != null && prevRevenue.compareTo(BigDecimal.ZERO) > 0) {
                    growth = revenue.subtract(prevRevenue)
                            .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }

                stats.add(new RevenueStatDTO("Q" + quarter, orders, revenue, growth));
                prevRevenue = revenue;
            }
        } else if (compareType == CompareType.SAME_PERIOD_LAST_YEAR) {
            // So với cùng kỳ năm trước
            List<Object[]> prev = orderRepository.revenueByQuarter(year - 1);
            Map<Integer, BigDecimal> prevMap = new HashMap<>();
            for (Object[] row : prev) {
                int quarter = ((Number) row[0]).intValue();
                BigDecimal revenue = (BigDecimal) row[2];
                prevMap.put(quarter, revenue);
            }

            for (Object[] row : current) {
                int quarter = ((Number) row[0]).intValue();
                long orders = ((Number) row[1]).longValue();
                BigDecimal revenue = (BigDecimal) row[2];

                BigDecimal prevRevenue = prevMap.getOrDefault(quarter, BigDecimal.ZERO);
                double growth = 0.0;
                if (prevRevenue.compareTo(BigDecimal.ZERO) > 0) {
                    growth = revenue.subtract(prevRevenue)
                            .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }

                stats.add(new RevenueStatDTO("Q" + quarter, orders, revenue, growth));
            }
        }

        return stats;
    }

    @Override
    public List<RevenueStatDTO> getRevenueByYear() {
        List<Object[]> results = orderRepository.revenueByYear();
        List<RevenueStatDTO> stats = new ArrayList<>();

        BigDecimal prevRevenue = null;
        for (Object[] row : results) {
            int year = ((Number) row[0]).intValue();
            long orders = ((Number) row[1]).longValue();
            BigDecimal revenue = (BigDecimal) row[2];

            double growth = 0.0;
            if (prevRevenue != null && prevRevenue.compareTo(BigDecimal.ZERO) > 0) {
                growth = revenue.subtract(prevRevenue)
                        .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }

            stats.add(new RevenueStatDTO(String.valueOf(year), orders, revenue, growth));
            prevRevenue = revenue;
        }
        return stats;
    }

    @Override
    public List<InstructorRevenueDTO> getInstructorRevenue(int year, Integer month, Integer quarter) {
        List<Object[]> results;

        if (month != null) {
            results = orderRepository.getInstructorRevenueByMonth(year, month);
        } else if (quarter != null) {
            results = orderRepository.getInstructorRevenueByQuarter(year, quarter);
        } else {
            results = orderRepository.getInstructorRevenueByYear(year);
        }

        List<InstructorRevenueDTO> dtos = new ArrayList<>();

        for (Object[] row : results) {
            Long instructorId = ((Number) row[0]).longValue();
            String instructorName = (String) row[1];
            Long totalCourses = ((Number) row[2]).longValue();
            Long totalLearners = ((Number) row[3]).longValue();
            BigDecimal totalRevenue = (BigDecimal) row[4];

            BigDecimal platformFee = totalRevenue.multiply(BigDecimal.valueOf(0.3));
            BigDecimal instructorIncome = totalRevenue.subtract(platformFee);

            dtos.add(new InstructorRevenueDTO(
                    instructorId,
                    instructorName,
                    totalCourses,
                    totalLearners,
                    totalRevenue,
                    platformFee,
                    instructorIncome,
                    0L,                 // withdrawalsCount (TODO)
                    BigDecimal.ZERO,    // totalWithdrawn (TODO)
                    BigDecimal.ZERO     // pendingWithdrawals (TODO)
            ));
        }

        return dtos;
    }

    @Override
    public List<CourseRevenueDTO> getCourseRevenueByInstructor(Long instructorId, int year, Integer month, Integer quarter) {
        List<Object[]> results;

        if (month != null) {
            results = orderRepository.getCourseRevenueByInstructorMonth(instructorId, year, month);
        } else if (quarter != null) {
            results = orderRepository.getCourseRevenueByInstructorQuarter(instructorId, year, quarter);
        } else if (year > 0) {
            results = orderRepository.getCourseRevenueByInstructorYear(instructorId, year);
        } else {
            results = orderRepository.getCourseRevenueByInstructor(instructorId);
        }

        List<CourseRevenueDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            Long courseId = ((Number) row[0]).longValue();
            String courseTitle = (String) row[1];
            Long totalLearners = ((Number) row[2]).longValue();
            BigDecimal totalRevenue = (BigDecimal) row[3];
            BigDecimal platformFee = totalRevenue.multiply(BigDecimal.valueOf(0.3));
            BigDecimal instructorIncome = totalRevenue.subtract(platformFee);

            dtos.add(new CourseRevenueDTO(
                    courseId,
                    courseTitle,
                    totalLearners,
                    totalRevenue,
                    platformFee,
                    instructorIncome
            ));
        }

        return dtos;
    }


}