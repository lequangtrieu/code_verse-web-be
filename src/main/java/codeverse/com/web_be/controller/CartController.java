package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.AddToCartRequest.AddToCartRequest;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.OrderDetail;
import codeverse.com.web_be.service.OrderService.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final OrderServiceImpl cartService;

    @GetMapping("/countCartDetail")
    public ResponseEntity<ApiResponse<Integer>> countCartDetail(@RequestParam String username) {
        int count = cartService.countCartDetail(username);
        return ResponseEntity.ok(
                ApiResponse.<Integer>builder()
                        .result(count)
                        .message("Success")
                        .build()
        );
    }


    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .result(cartService.addToCart(request.getUsername(), request.getCourseId()))
                        .message("Course added to cart")
                        .build()
        );
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<List<OrderDetail>>> getCartDetails(@RequestParam String username) {
        List<OrderDetail> cartDetails = cartService.getCartDetails(username);
        return ResponseEntity.ok(
                ApiResponse.<List<OrderDetail>>builder()
                        .result(cartDetails)
                        .message("Cart details fetched successfully")
                        .build()
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<String>> removeCartItem(@RequestParam Long orderDetailId) {
        cartService.removeCartItem(orderDetailId);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Item removed from cart")
                        .result("success")
                        .build()
        );
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(@RequestParam String username) {
        cartService.clearCart(username);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .result("Cart cleared successfully")
                        .message("Success")
                        .build()
        );
    }

}
