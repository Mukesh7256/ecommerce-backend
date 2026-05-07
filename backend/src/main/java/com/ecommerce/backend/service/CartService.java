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

    // ✅ T035 + T042 - Add to Cart with Validation
    public Cart addToCart(String userEmail,
                          Long productId, int quantity) {

        // Check product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found!")
                );

        // T042 - Validate stock
        if (product.getQuantity() <= 0) {
            throw new RuntimeException(
                    "Sorry! This product is out of stock!"
            );
        }

        // T042 - Validate requested quantity vs stock
        if (quantity > product.getQuantity()) {
            throw new RuntimeException(
                    "Only " + product.getQuantity() +
                            " items available in stock!"
            );
        }

        // Check if product already in cart
        Optional<Cart> existing = cartRepository
                .findByUserEmailAndProductId(userEmail, productId);

        if (existing.isPresent()) {
            Cart cart = existing.get();
            int newQty = cart.getQuantity() + quantity;

            // T042 - Validate total cart quantity
            if (newQty > product.getQuantity()) {
                throw new RuntimeException(
                        "Cannot add more! Only " +
                                product.getQuantity() +
                                " items available in stock!"
                );
            }

            if (newQty > 10) {
                throw new RuntimeException(
                        "Maximum 10 items allowed per product!"
                );
            }

            cart.setQuantity(newQty);
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

    // ✅ T037 + T042 - Update Cart with Validation
    public Cart updateCartItem(Long cartId, int quantity) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found!")
                );

        // T042 - Remove if quantity is 0
        if (quantity <= 0) {
            cartRepository.deleteById(cartId);
            return null;
        }

        // T042 - Validate max quantity
        if (quantity > 10) {
            throw new RuntimeException(
                    "Maximum 10 items allowed per product!"
            );
        }

        // T042 - Validate against stock
        Product product = cart.getProduct();
        if (quantity > product.getQuantity()) {
            throw new RuntimeException(
                    "Only " + product.getQuantity() +
                            " items available in stock!"
            );
        }

        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    // ✅ T038 - Remove from Cart
    public void removeFromCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Cart item not found!");
        }
        cartRepository.deleteById(cartId);
    }

    // BONUS - Clear entire cart
    public void clearCart(String userEmail) {
        cartRepository.deleteByUserEmail(userEmail);
    }

    // BONUS - Get cart total
    public Double getCartTotal(String userEmail) {
        List<Cart> items = cartRepository
                .findByUserEmail(userEmail);
        return items.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity()
                )
                .sum();
    }

    // BONUS - Get cart count
    public int getCartCount(String userEmail) {
        return cartRepository.countByUserEmail(userEmail);
    }
}