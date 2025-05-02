package codeverse.com.web_be.service.CartService;

import codeverse.com.web_be.dto.response.CheckoutResponse.CheckoutResponse;
import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.OrderStatus;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PayOS payOS;
    private final FunctionHelper functionHelper;

    @Override
    @Transactional
    public String addToCart(String username, Long courseId) {
        User user = functionHelper.getActiveUserByUsername(username);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        BigDecimal discountedPrice = course.getPrice()
                .multiply(BigDecimal.valueOf(1).subtract(course.getDiscount().divide(BigDecimal.valueOf(100))));

        if (discountedPrice.compareTo(BigDecimal.ZERO) == 0) {
            return "This course is free and doesn't need to be added to cart";
        }

        List<Order> paidOrders = orderRepository.findByUserAndStatus(user, OrderStatus.PAID);
        boolean alreadyPurchased = paidOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(orderItem -> orderItem.getCourse().getId().equals(course.getId()));

        if (alreadyPurchased) {
            return "You already own this course";

        }


            Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .cartItems(new ArrayList<>())
                                .build()
                ));

        boolean alreadyInCart = cart.getCartItems().stream()
                .anyMatch(item -> item.getCourse().getId().equals(course.getId()));

        if (alreadyInCart) {
            return "Course already in cart";
        }


        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .course(course)
                .build();

        cartItemRepository.save(cartItem);
        return "Added to cart successfully";
    }

    @Override
    @Transactional
    public Void addToCartFree(String username, Long courseId) {
        User user = functionHelper.getActiveUserByUsername(username);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_ENTITY));

        BigDecimal discountedPrice = course.getPrice()
                .multiply(BigDecimal.ONE.subtract(course.getDiscount().divide(BigDecimal.valueOf(100))));

        if (discountedPrice.compareTo(BigDecimal.ZERO) > 0) {
            throw new AppException(ErrorCode.COURSE_NOT_FREE);
        }

        List<Order> paidOrders = orderRepository.findByUserAndStatus(user, OrderStatus.PAID);
        boolean alreadyPurchased = paidOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(orderItem -> orderItem.getCourse().getId().equals(course.getId()));

        if (alreadyPurchased) {
            throw new AppException(ErrorCode.COURSE_ALREADY_OWNED);
        }

        Order order = Order.builder()
                .user(user)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PAID)
                .orderDate(LocalDateTime.now())
                .build();
        orderRepository.save(order);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .course(course)
                .priceAtPurchase(BigDecimal.ZERO)
                .build();
        orderItemRepository.save(orderItem);

        return null;
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId, String username) {
        functionHelper.getActiveUserByUsername(username);
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(String username) {
        User user = functionHelper.getActiveUserByUsername(username);

        Cart cart = cartRepository.findByUser(user)
                .orElse(null);

        if (cart != null && cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
            cartRepository.save(cart);
        }
    }

    @Override
    public List<CartItem> getCartDetails(String username) {
        User user = functionHelper.getActiveUserByUsername(username);

        return cartRepository.findByUser(user)
                .map(Cart::getCartItems)
                .orElse(Collections.emptyList());
    }

    @Override
    public int countCartItems(String username) {
        User user = functionHelper.getActiveUserByUsername(username);

        return cartRepository.findByUser(user)
                .map(cart -> cart.getCartItems().size())
                .orElse(0);
    }

    public CheckoutResponse checkoutWithPayOS(String username, List<Long> selectedCartItemIds) {
        if (selectedCartItemIds == null || selectedCartItemIds.isEmpty()) {
            throw new AppException(ErrorCode.ILLEGAL_ARGS);
        }

        User user = functionHelper.getActiveUserByUsername(username);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED_ENTITY));

        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> selectedCartItemIds.contains(item.getId()))
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new AppException(ErrorCode.ILLEGAL_ARGS);
        }

        boolean hasFreeCourse = selectedItems.stream()
                .map(CartItem::getCourse)
                .anyMatch(course -> course.getPrice()
                        .multiply(BigDecimal.valueOf(1)
                                .subtract(course.getDiscount().divide(BigDecimal.valueOf(100))))
                        .compareTo(BigDecimal.ZERO) == 0);

        if (hasFreeCourse) {
            throw new AppException(ErrorCode.FREE_COURSE_IN_CART);
        }

        BigDecimal total = selectedItems.stream()
                .map(item -> {
                    Course course = item.getCourse();
                    return course.getPrice().multiply(
                            BigDecimal.ONE.subtract(course.getDiscount().divide(BigDecimal.valueOf(100)))
                    );
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .build();
        orderRepository.save(order);

        for (CartItem cartItem : selectedItems) {
            Course course = cartItem.getCourse();
            BigDecimal discountedPrice = course.getPrice().multiply(
                    BigDecimal.ONE.subtract(course.getDiscount().divide(BigDecimal.valueOf(100)))
            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .course(course)
                    .priceAtPurchase(discountedPrice)
                    .build();

            orderItemRepository.save(orderItem);
        }

        long orderCode = System.currentTimeMillis();

        try {
            PaymentData paymentRequest = PaymentData.builder()
                    .amount(total.intValue())
                    .orderCode(orderCode)
                    .description("CodeVerse Course Payment")
                    .returnUrl("http://localhost:3000/payment-success?orderCode=" + orderCode + "&orderId=" + order.getId())
                    .cancelUrl("http://localhost:3000/payment-failed?orderCode=" + orderCode + "&orderId=" + order.getId())
                    .build();

            CheckoutResponseData paymentLink = payOS.createPaymentLink(paymentRequest);

            return CheckoutResponse.builder()
                    .checkoutUrl(paymentLink.getCheckoutUrl())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Transactional
    public void updateOrderStatusToPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        List<Long> purchasedCourseIds = order.getOrderItems()
                .stream()
                .map(orderItem -> orderItem.getCourse().getId())
                .toList();

        Cart cart = cartRepository.findByUser(order.getUser())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteByCartIdAndCourseIds(cart.getId(), purchasedCourseIds);
    }

    @Transactional
    public void clearOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderItemRepository.deleteAllByOrder(order);
        orderRepository.delete(order);
    }

}
