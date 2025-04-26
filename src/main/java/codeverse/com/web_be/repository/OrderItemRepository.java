package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Order;
import codeverse.com.web_be.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteAllByOrder(Order order);
}
