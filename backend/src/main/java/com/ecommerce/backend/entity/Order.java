package com.ecommerce.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;


// T043: Create Order entity
// T044: Design DB relationships
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user placed this order
    private String userEmail;
    private String userName;

    // T044: One order has many items
    @JsonBackReference
    @OneToMany(mappedBy = "order",
               cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    // T046: Total price calculated
    private Double totalPrice;
    private Double discountAmount;
    private Double finalPrice;

    // Order status
    private String status; // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    // Delivery details
    private String deliveryAddress;
    private String city;
    private String state;
    private String pincode;
    private String phone;

    // Payment
    private String paymentMethod; // COD, ONLINE
    private String paymentStatus; // PENDING, PAID

    // Timestamps
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
}