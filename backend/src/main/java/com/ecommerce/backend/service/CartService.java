package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    // Add item to cart
    public Cart addToCart(String userEmail, Long productId, int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        // Check if product already in cart
        Optional<Cart> existing = cartRepository
                .findByUserEmailAndProductId(userEmail, productId);

        if (existing.isPresent()) {
            // Update quantity if already exists
            Cart cart = existing.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepository.save(cart);
        }

        // Add new cart item
        Cart cart = new Cart();
        cart.setUserEmail(userEmail);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    // Get all cart items for user
    public List<Cart> getCartItems(String userEmail) {
        return cartRepository.findByUserEmail(userEmail);
    }

    // Remove item from cart
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    // Clear entire cart
    public void clearCart(String userEmail) {
        cartRepository.deleteByUserEmail(userEmail);
    }

    // Get cart total price
    public Double getCartTotal(String userEmail) {
        List<Cart> items = cartRepository.findByUserEmail(userEmail);
        return items.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity()
                )
                .sum();
    }

    // Get cart item count
    public int getCartCount(String userEmail) {
        return cartRepository.countByUserEmail(userEmail);
    }
}
