package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserEmail(String userEmail);

    Optional<Cart> findByUserEmailAndProductId(
        String userEmail, Long productId
    );

    // T048: Transaction needed for delete
    @Transactional
    void deleteByUserEmail(String userEmail);

    int countByUserEmail(String userEmail);
}