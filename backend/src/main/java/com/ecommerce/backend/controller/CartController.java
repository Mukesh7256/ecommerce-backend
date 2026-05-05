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

    // Helper - get logged in user email from JWT
    private String getEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    // ✅ T035 - Add to Cart
    // POST http://localhost:8080/api/cart/add
    // Body: { "productId": 1, "quantity": 2 }
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestBody Map<String, Object> request) {
        try {
            String email = getEmail();
            Long productId = Long.valueOf(
                    request.get("productId").toString()
            );
            int quantity = Integer.parseInt(
                    request.get("quantity").toString()
            );
            Cart cart = cartService.addToCart(
                    email, productId, quantity
            );
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // ✅ T036 - Get My Cart
    // GET http://localhost:8080/api/cart
    @GetMapping
    public ResponseEntity<List<Cart>> getMyCart() {
        String email = getEmail();
        List<Cart> items = cartService.getCartItems(email);
        return ResponseEntity.ok(items);
    }

    // ✅ T037 - Update Cart Item Quantity
    // PUT http://localhost:8080/api/cart/update/1
    // Body: { "quantity": 3 }
    @PutMapping("/update/{cartId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long cartId,
            @RequestBody Map<String, Object> request) {
        try {
            int quantity = Integer.parseInt(
                    request.get("quantity").toString()
            );
            Cart updated = cartService.updateCartItem(
                    cartId, quantity
            );
            if (updated == null) {
                return ResponseEntity.ok(
                        "Item removed because quantity was 0"
                );
            }
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // ✅ T038 - Remove from Cart
    // DELETE http://localhost:8080/api/cart/1
    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> removeItem(
            @PathVariable Long cartId) {
        try {
            cartService.removeFromCart(cartId);
            return ResponseEntity.ok(
                    "Item removed from cart successfully!"
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // BONUS - Clear entire cart
    // DELETE http://localhost:8080/api/cart/clear
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        String email = getEmail();
        cartService.clearCart(email);
        return ResponseEntity.ok("Cart cleared successfully!");
    }

    // BONUS - Get cart total price
    // GET http://localhost:8080/api/cart/total
    @GetMapping("/total")
    public ResponseEntity<Double> getTotal() {
        String email = getEmail();
        Double total = cartService.getCartTotal(email);
        return ResponseEntity.ok(total);
    }

    // BONUS - Get cart item count
    // GET http://localhost:8080/api/cart/count
    @GetMapping("/count")
    public ResponseEntity<Integer> getCount() {
        String email = getEmail();
        int count = cartService.getCartCount(email);
        return ResponseEntity.ok(count);
    }
}