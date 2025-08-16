package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.WithdrawalResponse.InstructorIncomeDTO;
import codeverse.com.web_be.entity.Order;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserAndStatus(User user, OrderStatus status);

    List<Order> findAllByStatus(OrderStatus status);

    @Query("SELECT new codeverse.com.web_be.dto.response.WithdrawalResponse.InstructorIncomeDTO(" +
            "c.id, c.title, u.name, oi.priceAtPurchase, o.orderDate) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "JOIN o.user u " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "AND c.instructor.id = :instructorId")
    List<InstructorIncomeDTO> getInstructorIncome(@Param("instructorId") Long instructorId);


    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal sumTotalAmount();

    @Query("SELECT FUNCTION('YEAR', o.orderDate), SUM(o.totalAmount) FROM Order o GROUP BY FUNCTION('YEAR', o.orderDate) ORDER BY FUNCTION('YEAR', o.orderDate)")
    List<Object[]> getRevenueByYearRaw();

    @Query("SELECT FUNCTION('MONTH', o.orderDate), FUNCTION('YEAR', o.orderDate), SUM(o.totalAmount) " +
            " FROM Order o GROUP BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate) " +
            " ORDER BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)")
    List<Object[]> getRevenueByMonthRaw();

    @Query("SELECT CEIL(MONTH(o.orderDate) / 3.0), FUNCTION('YEAR', o.orderDate), SUM(o.totalAmount) " +
            " FROM Order o GROUP BY FUNCTION('YEAR', o.orderDate), CEIL(MONTH(o.orderDate) / 3.0) " +
            " ORDER BY FUNCTION('YEAR', o.orderDate), CEIL(MONTH(o.orderDate) / 3.0)")
    List<Object[]> getRevenueByQuarterRaw();

    //query doanh thu theo từng instructor
    @Query("SELECT c.instructor.id, c.instructor.name, " +
            "COUNT(DISTINCT c.id), " +
            "COUNT(DISTINCT o.user.id), " +
            "COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "GROUP BY c.instructor.id, c.instructor.name")
    List<Object[]> getInstructorRevenueSummary();

    // Doanh thu theo instructor - filter theo YEAR
    @Query("SELECT c.instructor.id, c.instructor.name, " +
            "COUNT(DISTINCT c.id), " +
            "COUNT(DISTINCT o.user.id), " +
            "COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "AND FUNCTION('YEAR', o.orderDate) = :year " +
            "GROUP BY c.instructor.id, c.instructor.name")
    List<Object[]> getInstructorRevenueByYear(@Param("year") int year);

    // Doanh thu theo instructor - filter theo MONTH + YEAR
    @Query("SELECT c.instructor.id, c.instructor.name, " +
            "COUNT(DISTINCT c.id), " +
            "COUNT(DISTINCT o.user.id), " +
            "COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "AND FUNCTION('YEAR', o.orderDate) = :year " +
            "AND FUNCTION('MONTH', o.orderDate) = :month " +
            "GROUP BY c.instructor.id, c.instructor.name")
    List<Object[]> getInstructorRevenueByMonth(@Param("year") int year, @Param("month") int month);

    // Doanh thu theo instructor - filter theo QUARTER + YEAR
    @Query("SELECT c.instructor.id, c.instructor.name, " +
            "COUNT(DISTINCT c.id), " +
            "COUNT(DISTINCT o.user.id), " +
            "COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "AND FUNCTION('YEAR', o.orderDate) = :year " +
            "AND CEIL(MONTH(o.orderDate) / 3.0) = :quarter " +
            "GROUP BY c.instructor.id, c.instructor.name")
    List<Object[]> getInstructorRevenueByQuarter(@Param("year") int year, @Param("quarter") int quarter);

    // Doanh thu từng khóa của instructor - filter theo YEAR
    @Query("SELECT c.id, c.title, COUNT(DISTINCT o.user.id), COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "AND c.instructor.id = :instructorId " +
            "AND FUNCTION('YEAR', o.orderDate) = :year " +
            "GROUP BY c.id, c.title")
    List<Object[]> getCourseRevenueByInstructorYear(
            @Param("instructorId") Long instructorId,
            @Param("year") int year);

    //query doanh thu từng khóa
    @Query("SELECT c.id, c.title, COUNT(DISTINCT o.user.id), COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "AND c.instructor.id = :instructorId " +
            "GROUP BY c.id, c.title")
    List<Object[]> getCourseRevenueByInstructor(@Param("instructorId") Long instructorId);

    // Doanh thu từng khóa của instructor - filter theo MONTH + YEAR
    @Query("SELECT c.id, c.title, COUNT(DISTINCT o.user.id), COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "AND c.instructor.id = :instructorId " +
            "AND FUNCTION('YEAR', o.orderDate) = :year " +
            "AND FUNCTION('MONTH', o.orderDate) = :month " +
            "GROUP BY c.id, c.title")
    List<Object[]> getCourseRevenueByInstructorMonth(
            @Param("instructorId") Long instructorId,
            @Param("year") int year,
            @Param("month") int month);

    // Doanh thu từng khóa của instructor - filter theo QUARTER + YEAR
    @Query("SELECT c.id, c.title, COUNT(DISTINCT o.user.id), COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.course c " +
            "WHERE o.status = codeverse.com.web_be.enums.OrderStatus.PAID " +
            "AND c.instructor.id = :instructorId " +
            "AND FUNCTION('YEAR', o.orderDate) = :year " +
            "AND CEIL(MONTH(o.orderDate) / 3.0) = :quarter " +
            "GROUP BY c.id, c.title")
    List<Object[]> getCourseRevenueByInstructorQuarter(
            @Param("instructorId") Long instructorId,
            @Param("year") int year,
            @Param("quarter") int quarter);

}

