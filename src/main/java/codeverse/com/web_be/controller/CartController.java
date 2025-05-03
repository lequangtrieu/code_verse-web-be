package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.AddToCartRequest.AddToCartRequest;
import codeverse.com.web_be.dto.request.AddToCartRequest.PaymentConfirmationRequest;
import codeverse.com.web_be.dto.response.CheckoutResponse.CheckoutResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.CartItem;
import codeverse.com.web_be.service.CartService.CartServiceImpl;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartService;

    @GetMapping("/countCartDetail")
    public ResponseEntity<ApiResponse<Integer>> countCartItems(@RequestParam String username) {
        int count = cartService.countCartItems(username);
        return ResponseEntity.ok(
                ApiResponse.<Integer>builder()
                        .message("Cart item count fetched successfully")
                        .result(count)
                        .build()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addToCart(@RequestBody AddToCartRequest request) {
        String result = cartService.addToCart(request.getUsername(), request.getCourseId());
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Course added to cart successfully")
                        .result(result)
                        .build()
        );
    }

    @PostMapping("/addFree")
    public Void addToCartFree(@RequestBody AddToCartRequest request) {
        cartService.addToCartFree(request.getUsername(), request.getCourseId());
        return null;
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<List<CartItem>>> getCartDetails(@RequestParam String username) {
        List<CartItem> cartDetails = cartService.getCartDetails(username);
        return ResponseEntity.ok(
                ApiResponse.<List<CartItem>>builder()
                        .message("Cart details fetched successfully")
                        .result(cartDetails)
                        .build()
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<String>> removeCartItem(@RequestParam Long cartItemId, @RequestParam String username) {
        cartService.removeCartItem(cartItemId, username);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Removed item from cart successfully")
                        .result("Success")
                        .build()
        );
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(@RequestParam String username) {
        cartService.clearCart(username);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Cart cleared successfully")
                        .result("Success")
                        .build()
        );
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(@RequestBody AddToCartRequest request) {
        CheckoutResponse response  = cartService.checkoutWithPayOS(request.getUsername(), request.getSelectedCartItemId());
        return ResponseEntity.ok(
                ApiResponse.<CheckoutResponse>builder()
                        .result(response)
                        .message("Redirecting to PayOS")
                        .build()
        );
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<ApiResponse<String>> confirmPayment(@RequestBody PaymentConfirmationRequest request) {
        if ("success".equals(request.getStatus())) {
            cartService.updateOrderStatusToPaid(request.getOrderId());
        } else if ("failed".equals(request.getStatus())) {
            cartService.clearOrder(request.getOrderId());
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .message("Invalid status")
                    .build());
        }

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Payment status updated")
                .result("Success")
                .build());
    }

}
