package com.ecommerce.backend.controller;

import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    @Autowired
    private CartService cartService;

    // Helper to get logged in user email
    private String getEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    // POST - Add to cart
    // POST http://localhost:8080/api/cart/add
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestBody Map<String, Object> request) {
        String email = getEmail();
        Long productId = Long.valueOf(
                request.get("productId").toString()
        );
        int quantity = Integer.parseInt(
                request.get("quantity").toString()
        );
        Cart cart = cartService.addToCart(email, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    // GET - Get my cart
    // GET http://localhost:8080/api/cart
    @GetMapping
    public ResponseEntity<List<Cart>> getMyCart() {
        String email = getEmail();
        List<Cart> items = cartService.getCartItems(email);
        return ResponseEntity.ok(items);
    }

    // DELETE - Remove one item
    // DELETE http://localhost:8080/api/cart/1
    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> removeItem(@PathVariable Long cartId) {
        cartService.removeFromCart(cartId);
        return ResponseEntity.ok("Item removed from cart!");
    }

    // DELETE - Clear entire cart
    // DELETE http://localhost:8080/api/cart/clear
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        String email = getEmail();
        cartService.clearCart(email);
        return ResponseEntity.ok("Cart cleared!");
    }

    // GET - Get cart total
    // GET http://localhost:8080/api/cart/total
    @GetMapping("/total")
    public ResponseEntity<Double> getTotal() {
        String email = getEmail();
        Double total = cartService.getCartTotal(email);
        return ResponseEntity.ok(total);
    }

    // GET - Get cart count
    // GET http://localhost:8080/api/cart/count
    @GetMapping("/count")
    public ResponseEntity<Integer> getCount() {
        String email = getEmail();
        int count = cartService.getCartCount(email);
        return ResponseEntity.ok(count);
    }
}
