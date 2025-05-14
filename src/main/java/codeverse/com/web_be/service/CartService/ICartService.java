package codeverse.com.web_be.service.CartService;

import codeverse.com.web_be.entity.Cart;

import java.util.List;

public interface ICartService {
    String addToCart(String username, Long courseId);
    Void addToCartFree(String username, Long courseId);
    void removeCartItem(Long cartItemId, String username);
    void clearCart(String username);
    List<Cart> getCartDetails(String username);
    int countCartItems(String username);
}
