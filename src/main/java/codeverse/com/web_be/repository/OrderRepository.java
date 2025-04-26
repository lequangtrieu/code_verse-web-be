package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

