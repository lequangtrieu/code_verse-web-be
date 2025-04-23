package codeverse.com.web_be.service.OrderService;

import codeverse.com.web_be.entity.OrderDetail;

import java.util.List;

public interface IOrderService {
    String addToCart(String username, Long courseId);
    List<OrderDetail> getCartDetails(String username);
    void removeCartItem(Long courseId);
    void clearCart(String  username);
}
