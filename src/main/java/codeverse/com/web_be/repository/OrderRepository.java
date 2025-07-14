package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Order;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserAndStatus(User user, OrderStatus status);

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
}

