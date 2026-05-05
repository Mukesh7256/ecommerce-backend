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

    // ✅ T035 - Add to Cart
    public Cart addToCart(String userEmail, Long productId, int quantity) {

        // Check product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found!")
                );

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

    // ✅ T036 - Get Cart Items
    public List<Cart> getCartItems(String userEmail) {
        return cartRepository.findByUserEmail(userEmail);
    }

    // ✅ T037 - Update Cart Item Quantity
    public Cart updateCartItem(Long cartId, int quantity) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found!")
                );

        // If quantity is 0 or less → remove item
        if (quantity <= 0) {
            cartRepository.deleteById(cartId);
            return null;
        }

        // Update quantity
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    // ✅ T038 - Remove from Cart
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    // BONUS - Clear entire cart
    public void clearCart(String userEmail) {
        cartRepository.deleteByUserEmail(userEmail);
    }

    // BONUS - Get cart total price
    public Double getCartTotal(String userEmail) {
        List<Cart> items = cartRepository.findByUserEmail(userEmail);
        return items.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity()
                )
                .sum();
    }

    // BONUS - Get cart item count
    public int getCartCount(String userEmail) {
        return cartRepository.countByUserEmail(userEmail);
    }
}