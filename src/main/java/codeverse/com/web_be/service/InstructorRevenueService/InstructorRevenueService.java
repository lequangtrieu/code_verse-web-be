package codeverse.com.web_be.service.InstructorRevenueService;

import codeverse.com.web_be.dto.response.DashboardResponse.InstructorRevenueDTO;
import codeverse.com.web_be.repository.OrderRepository;
import codeverse.com.web_be.repository.WithdrawalRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorRevenueService {

    private final OrderRepository orderRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;

//    public List<InstructorRevenueDTO> getAllInstructorRevenue() {
//        List<Object[]> revenueData = orderRepository.getInstructorRevenueSummary();
//        Map<Long, Object[]> approvedMap = withdrawalRequestRepository.getApprovedWithdrawals()
//                .stream()
//                .collect(Collectors.toMap(r -> (Long) r[0], r -> r));
//        Map<Long, BigDecimal> pendingMap = withdrawalRequestRepository.getPendingWithdrawals()
//                .stream()
//                .collect(Collectors.toMap(r -> (Long) r[0], r -> (BigDecimal) r[1]));
//
//        List<InstructorRevenueDTO> result = new ArrayList<>();
//        for (Object[] row : revenueData) {
//            Long instructorId = (Long) row[0];
//            String name = (String) row[1];
//            Long totalCourses = (Long) row[2];
//            Long totalLearners = (Long) row[3];
//            BigDecimal totalRevenue = (BigDecimal) row[4];
//
//            BigDecimal platformFee = totalRevenue.multiply(BigDecimal.valueOf(0.3));
//            BigDecimal instructorIncome = totalRevenue.subtract(platformFee);
//
//            Long withdrawalsCount = 0L;
//            BigDecimal totalWithdrawn = BigDecimal.ZERO;
//            BigDecimal pendingWithdrawals = BigDecimal.ZERO;
//
//            if (approvedMap.containsKey(instructorId)) {
//                withdrawalsCount = (Long) approvedMap.get(instructorId)[1];
//                totalWithdrawn = (BigDecimal) approvedMap.get(instructorId)[2];
//            }
//            if (pendingMap.containsKey(instructorId)) {
//                pendingWithdrawals = pendingMap.get(instructorId);
//            }
//
//            result.add(new InstructorRevenueDTO(
//                    instructorId, name, totalCourses, totalLearners,
//                    totalRevenue, platformFee, instructorIncome,
//                    withdrawalsCount, totalWithdrawn, pendingWithdrawals
//            ));
//        }
//        return result;
//    }

    private List<InstructorRevenueDTO> mapToInstructorRevenueDTO(List<Object[]> revenueData) {
        Map<Long, Object[]> approvedMap = withdrawalRequestRepository.getApprovedWithdrawals()
                .stream()
                .collect(Collectors.toMap(r -> (Long) r[0], r -> r));
        Map<Long, BigDecimal> pendingMap = withdrawalRequestRepository.getPendingWithdrawals()
                .stream()
                .collect(Collectors.toMap(r -> (Long) r[0], r -> (BigDecimal) r[1]));

        List<InstructorRevenueDTO> result = new ArrayList<>();
        for (Object[] row : revenueData) {
            Long instructorId = (Long) row[0];
            String name = (String) row[1];
            Long totalCourses = (Long) row[2];
            Long totalLearners = (Long) row[3];
            BigDecimal totalRevenue = (BigDecimal) row[4];

            BigDecimal platformFee = totalRevenue.multiply(BigDecimal.valueOf(0.3));
            BigDecimal instructorIncome = totalRevenue.subtract(platformFee);

            Long withdrawalsCount = 0L;
            BigDecimal totalWithdrawn = BigDecimal.ZERO;
            BigDecimal pendingWithdrawals = BigDecimal.ZERO;

            if (approvedMap.containsKey(instructorId)) {
                withdrawalsCount = (Long) approvedMap.get(instructorId)[1];
                totalWithdrawn = (BigDecimal) approvedMap.get(instructorId)[2];
            }
            if (pendingMap.containsKey(instructorId)) {
                pendingWithdrawals = pendingMap.get(instructorId);
            }

            result.add(new InstructorRevenueDTO(
                    instructorId, name, totalCourses, totalLearners,
                    totalRevenue, platformFee, instructorIncome,
                    withdrawalsCount, totalWithdrawn, pendingWithdrawals
            ));
        }
        return result;
    }

    public List<InstructorRevenueDTO> getInstructorRevenue(String type, Integer year, Integer month, Integer quarter) {
        List<Object[]> data;

        if ("year".equalsIgnoreCase(type) && year != null) {
            data = orderRepository.getInstructorRevenueByYear(year);
        } else if ("month".equalsIgnoreCase(type) && year != null && month != null) {
            data = orderRepository.getInstructorRevenueByMonth(year, month);
        } else if ("quarter".equalsIgnoreCase(type) && year != null && quarter != null) {
            data = orderRepository.getInstructorRevenueByQuarter(year, quarter);
        } else {
            data = orderRepository.getInstructorRevenueSummary(); // không filter
        }

        return mapToInstructorRevenueDTO(data);
    }

}
