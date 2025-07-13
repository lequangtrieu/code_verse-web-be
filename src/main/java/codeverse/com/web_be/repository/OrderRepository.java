package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.WithdrawalResponse.InstructorIncomeDTO;
import codeverse.com.web_be.entity.Order;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}

