package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Get all cart items for a user
    List<Cart> findByUserEmail(String userEmail);

    // Find specific product in user's cart
    Optional<Cart> findByUserEmailAndProductId(
            String userEmail, Long productId
    );

    // Delete all cart items for a user
    void deleteByUserEmail(String userEmail);

    // Count items in user's cart
    int countByUserEmail(String userEmail);
}