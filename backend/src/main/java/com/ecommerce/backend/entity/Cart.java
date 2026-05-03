package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user owns this cart item
    private String userEmail;

    // Which product is in cart
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // How many of this product
    private Integer quantity;
}
