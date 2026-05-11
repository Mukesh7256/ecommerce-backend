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

    // T047: Save order details
    // T048: Handle transaction logic
    @Transactional
    public Order checkout(String userEmail,
            CheckoutRequest request) {

        // Get cart items
        List<Cart> cartItems = cartRepository
            .findByUserEmail(userEmail);

        if (cartItems.isEmpty()) {
            throw new RuntimeException(
                "Cart is empty! Add items before checkout."
            );
        }

        // Get user
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() ->
                new RuntimeException("User not found!")
            );

        // T047: Create and save order
        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setUserName(user.getName());
        order.setStatus("CONFIRMED"); // ← Changed from PENDING
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

        // Save order first
        Order savedOrder = orderRepository.save(order);

        // T047: Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // T048: Validate stock
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                    "Insufficient stock for: " + product.getName()
                );
            }

            double itemTotal = product.getPrice()
                * cartItem.getQuantity();
            totalPrice += itemTotal;

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setItemTotal(itemTotal);
            orderItems.add(orderItem);

            // T048: Reduce stock
            product.setQuantity(
                product.getQuantity() - cartItem.getQuantity()
            );
            productRepository.save(product);
        }

        // T046: Calculate prices
        double discountAmount = Math.round(totalPrice * 0.10);
        double delivery = totalPrice > 499 ? 0 : 49;
        double finalPrice = totalPrice - discountAmount + delivery;

        // T047: Update order with all details
        savedOrder.setOrderItems(orderItems);
        savedOrder.setTotalPrice(totalPrice);
        savedOrder.setDiscountAmount(discountAmount);
        savedOrder.setFinalPrice(finalPrice);

        // T048: Clear cart after order
        cartRepository.deleteByUserEmail(userEmail);

        return orderRepository.save(savedOrder);
    }

    // T047: Get user orders
    public List<Order> getUserOrders(String userEmail) {
        return orderRepository
            .findByUserEmailOrderByOrderDateDesc(userEmail);
    }

    // T047: Get order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Order not found!")
            );
    }

    // Get all orders - Admin
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    // Update status - Admin
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // Cancel order
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

        // T048: Restore stock on cancel
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(
                product.getQuantity() + item.getQuantity()
            );
            productRepository.save(product);
        }

        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }
}