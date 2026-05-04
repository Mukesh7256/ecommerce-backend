package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private Double price;
    private Integer quantity;
    private String category;
    private String imageUrl;

    @Column(length = 2000)
    private String imageUrls;

    // ✅ NEW - Product Highlights (like Amazon)
    // Format: "Brand:Samsung,RAM:12GB,Storage:256GB"
    @Column(length = 2000)
    private String specifications;

    // ✅ NEW - Top Highlights bullets
    // Format: "Built-in Privacy|Knox Security|5G Ready"
    @Column(length = 2000)
    private String highlights;

    // ✅ NEW - Product brand
    private String brand;

    // ✅ NEW - Product rating (1-5)
    private Double rating;

    // ✅ NEW - Number of reviews
    private Integer reviewCount;
}