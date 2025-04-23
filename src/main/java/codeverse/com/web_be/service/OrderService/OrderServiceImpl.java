package codeverse.com.web_be.service.OrderService;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.OrderDetail;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.enums.OrderStatus;
import codeverse.com.web_be.repository.*;
import codeverse.com.web_be.service.GenericServiceImpl;

import codeverse.com.web_be.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl extends GenericServiceImpl<Order, Long> implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            UserRepository userRepository,
            CourseRepository courseRepository
    ) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }
    @Override
    public String addToCart(String username, Long courseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Optional<Order> existingCartOpt = orderRepository.findByUserAndIsCartTrue(user);

        Order cart = existingCartOpt.orElseGet(() -> {
            Order newCart = Order.builder()
                    .user(user)
                    .status(OrderStatus.PENDING)
                    .totalAmount(BigDecimal.ZERO)
                    .finalAmount(BigDecimal.ZERO)
                    .isCart(true)
                    .orderDetails(new ArrayList<>())
                    .build();
            return orderRepository.save(newCart);
        });

        boolean alreadyInCart = Optional.ofNullable(cart.getOrderDetails())
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(detail -> detail.getCourse().getId().equals(courseId));
        if (alreadyInCart) return "Course already in cart";

        BigDecimal discount = course.getPrice().multiply(BigDecimal.valueOf(0));
        BigDecimal finalPrice = course.getPrice().subtract(discount);

        OrderDetail detail = OrderDetail.builder()
                .order(cart)
                .course(course)
                .price(course.getPrice())
                .discount(discount)
                .finalPrice(finalPrice)
                .build();

        orderDetailRepository.save(detail);

        cart.setTotalAmount(cart.getTotalAmount().add(course.getPrice()));
        cart.setFinalAmount(cart.getFinalAmount().add(finalPrice));
        orderRepository.save(cart);

        return "Added successfully";
    }

    @Override
    public void removeCartItem(Long orderDetailId) {
        orderDetailRepository.deleteById(orderDetailId);
    }

    @Override
    @Transactional
    public void clearCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Order> optionalOrder = orderRepository.findByUserAndIsCartTrue(user);

        optionalOrder.ifPresent(order -> {
            List<OrderDetail> details = order.getOrderDetails();
            if (details != null) {
                details.clear();
            }

            order.setTotalAmount(BigDecimal.ZERO);
            order.setFinalAmount(BigDecimal.ZERO);
            orderRepository.save(order);
        });
    }

    @Override
    public List<OrderDetail> getCartDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Order> orderOpt = orderRepository.findByUserAndIsCartTrue(user);

        List<OrderDetail> orderDetails = orderOpt.map(Order::getOrderDetails).orElse(List.of());
        orderDetails.forEach(detail -> {
            if (detail.getCourse() != null) {
                detail.getCourse().getTitle();
            }
        });

        return orderDetails;
    }

    public int countCartDetail(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserAndIsCartTrue(user)
                .map(order -> Optional.ofNullable(order.getOrderDetails()).orElse(Collections.emptyList()).size())
                .orElse(0);
    }
}
