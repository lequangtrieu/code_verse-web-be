package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.course.id IN :courseIds")
    void deleteByCartIdAndCourseIds(@Param("cartId") Long cartId, @Param("courseIds") List<Long> courseIds);
}
