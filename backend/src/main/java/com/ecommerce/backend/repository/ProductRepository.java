package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // T028: Search by name (case insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // T028: Filter by category
    List<Product> findByCategory(String category);

    // T028: Filter by max price
    List<Product> findByPriceLessThanEqual(Double maxPrice);

    // T028: Filter by category AND max price
    List<Product> findByCategoryAndPriceLessThanEqual(
            String category, Double maxPrice
    );

    // Custom query - search in name OR description
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(String keyword);
}