package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
