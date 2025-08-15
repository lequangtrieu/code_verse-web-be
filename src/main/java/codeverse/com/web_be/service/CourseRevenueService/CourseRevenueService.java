package codeverse.com.web_be.service.CourseRevenueService;

import codeverse.com.web_be.dto.response.DashboardResponse.CourseRevenueDTO;
import codeverse.com.web_be.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRevenueService {

    private final OrderRepository orderRepository;

    public List<CourseRevenueDTO> getCourseRevenue(Long instructorId) {
        List<Object[]> data = orderRepository.getCourseRevenueByInstructor(instructorId);

        return data.stream().map(row -> {
            Long courseId = (Long) row[0];
            String title = (String) row[1];
            Long totalLearners = (Long) row[2];
            BigDecimal totalRevenue = (BigDecimal) row[3];
            BigDecimal platformFee = totalRevenue.multiply(BigDecimal.valueOf(0.3));
            BigDecimal instructorIncome = totalRevenue.subtract(platformFee);

            return new CourseRevenueDTO(courseId, title, totalLearners, totalRevenue, platformFee, instructorIncome);
        }).collect(Collectors.toList());
    }

    /**
     * Lấy doanh thu từng khóa học của instructor theo filter type
     */
    public List<CourseRevenueDTO> getCourseRevenueByInstructor(
            Long instructorId,
            String type,
            Integer year,
            Integer month,
            Integer quarter
    ) {
        List<Object[]> data;

        if ("year".equalsIgnoreCase(type) && year != null) {
            data = orderRepository.getCourseRevenueByInstructorYear(instructorId, year);
        } else if ("month".equalsIgnoreCase(type) && year != null && month != null) {
            data = orderRepository.getCourseRevenueByInstructorMonth(instructorId, year, month);
        } else if ("quarter".equalsIgnoreCase(type) && year != null && quarter != null) {
            data = orderRepository.getCourseRevenueByInstructorQuarter(instructorId, year, quarter);
        } else {
            data = orderRepository.getCourseRevenueByInstructor(instructorId);
        }

        return data.stream().map(row -> {
            Long courseId = ((Number) row[0]).longValue();
            String title = (String) row[1];
            Long totalLearners = ((Number) row[2]).longValue();
            BigDecimal totalRevenue = (BigDecimal) row[3];
            BigDecimal platformFee = totalRevenue.multiply(BigDecimal.valueOf(0.3));
            BigDecimal instructorIncome = totalRevenue.subtract(platformFee);

            return new CourseRevenueDTO(courseId, title, totalLearners, totalRevenue, platformFee, instructorIncome);
        }).collect(Collectors.toList());
    }


}

