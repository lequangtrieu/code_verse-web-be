package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Order;
import codeverse.com.web_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserAndIsCartTrue(User user);
}
