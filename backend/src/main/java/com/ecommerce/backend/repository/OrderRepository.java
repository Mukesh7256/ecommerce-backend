package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders for a user
    List<Order> findByUserEmailOrderByOrderDateDesc(String userEmail);

    // Get orders by status
    List<Order> findByStatus(String status);

    // Get all orders (admin)
    List<Order> findAllByOrderByOrderDateDesc();
}