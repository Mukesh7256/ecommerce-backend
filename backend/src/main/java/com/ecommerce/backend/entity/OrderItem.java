package com.ecommerce.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// T044: Order Item - relationship between Order and Product
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // T044: Many items belong to one order
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // T044: Each item has one product
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double itemTotal;
}