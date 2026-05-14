package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.CheckoutRequest;
import com.ecommerce.backend.entity.*;
import com.ecommerce.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // T051: Checkout integrated with backend
    // T052: Full validation
    @Transactional
    public Order checkout(String userEmail,
            CheckoutRequest request) {

        // T052: Validate cart not empty
        List<Cart> cartItems = cartRepository
            .findByUserEmail(userEmail);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException(
                "Cart is empty! Please add items first."
            );
        }

        // T052: Validate user exists
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() ->
                new RuntimeException("User not found!")
            );

        // T052: Validate payment method
        if (!request.getPaymentMethod().equals("COD") &&
            !request.getPaymentMethod().equals("ONLINE")) {
            throw new RuntimeException(
                "Invalid payment method! Use COD or ONLINE."
            );
        }

        // T051: Create order
        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setUserName(user.getName());
        order.setStatus("CONFIRMED");
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setCity(request.getCity());
        order.setState(request.getState());
        order.setPincode(request.getPincode());
        order.setPhone(request.getPhone());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(
            request.getPaymentMethod().equals("COD")
                ? "PENDING" : "PAID"
        );
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryDate(LocalDateTime.now().plusDays(5));

        Order savedOrder = orderRepository.save(order);

        // T051 + T052: Process each cart item with validation
        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // T052: Validate stock for each item
            if (product.getQuantity() <= 0) {
                throw new RuntimeException(
                    product.getName() + " is out of stock!"
                );
            }

            if (cartItem.getQuantity() > product.getQuantity()) {
                throw new RuntimeException(
                    "Only " + product.getQuantity() +
                    " units of " + product.getName() +
                    " available!"
                );
            }

            // Calculate item total
            double itemTotal = product.getPrice()
                * cartItem.getQuantity();
            totalPrice += itemTotal;

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setItemTotal(itemTotal);
            orderItems.add(orderItem);

            // Reduce stock
            product.setQuantity(
                product.getQuantity() - cartItem.getQuantity()
            );
            productRepository.save(product);
        }

        // T052: Validate total price
        if (totalPrice <= 0) {
            throw new RuntimeException(
                "Invalid order total!"
            );
        }

        // Calculate final price
        double discountAmount = Math.round(totalPrice * 0.10);
        double delivery = totalPrice > 499 ? 0 : 49;
        double finalPrice = totalPrice - discountAmount + delivery;

        // Update order
        savedOrder.setOrderItems(orderItems);
        savedOrder.setTotalPrice(totalPrice);
        savedOrder.setDiscountAmount(discountAmount);
        savedOrder.setFinalPrice(finalPrice);

        // Clear cart
        cartRepository.deleteByUserEmail(userEmail);

        return orderRepository.save(savedOrder);
    }

    public List<Order> getUserOrders(String userEmail) {
        return orderRepository
            .findByUserEmailOrderByOrderDateDesc(userEmail);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Order not found!")
            );
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        List<String> validStatuses = List.of(
            "PENDING", "CONFIRMED",
            "SHIPPED", "DELIVERED", "CANCELLED"
        );

        if (!validStatuses.contains(status)) {
            throw new RuntimeException(
                "Invalid status! Use: " +
                String.join(", ", validStatuses)
            );
        }

        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long id, String userEmail) {
        Order order = getOrderById(id);

        if (!order.getUserEmail().equals(userEmail)) {
            throw new RuntimeException(
                "You can only cancel your own orders!"
            );
        }

        if (order.getStatus().equals("DELIVERED")) {
            throw new RuntimeException(
                "Cannot cancel delivered order!"
            );
        }

        if (order.getStatus().equals("CANCELLED")) {
            throw new RuntimeException(
                "Order is already cancelled!"
            );
        }

        // Restore stock
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    product.setQuantity(
                        product.getQuantity() + item.getQuantity()
                    );
                    productRepository.save(product);
                }
            }
        }

        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }
}