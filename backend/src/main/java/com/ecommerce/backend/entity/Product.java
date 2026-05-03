package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

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
    private String description;
    private Double price;
    private Integer quantity;
    private String category;

    // Main image (thumbnail)
    private String imageUrl;

    // Multiple images like Amazon
    // Stored as comma separated URLs in DB
    @Column(length = 2000)
    private String imageUrls;

    // Helper method to get image list
    public List<String> getImageList() {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return imageUrl != null
                    ? List.of(imageUrl)
                    : List.of();
        }
        return List.of(imageUrls.split(","));
    }
}