package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Cart;
import codeverse.com.web_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}