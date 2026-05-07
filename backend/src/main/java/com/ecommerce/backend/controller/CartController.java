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

    // Helper - get logged in user email
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
            // T042 - Validate request
            if (request.get("productId") == null) {
                return ResponseEntity.badRequest()
                        .body("Product ID is required!");
            }
            if (request.get("quantity") == null) {
                return ResponseEntity.badRequest()
                        .body("Quantity is required!");
            }

            Long productId = Long.valueOf(
                    request.get("productId").toString()
            );
            int quantity = Integer.parseInt(
                    request.get("quantity").toString()
            );

            // T042 - Validate quantity
            if (quantity <= 0) {
                return ResponseEntity.badRequest()
                        .body("Quantity must be at least 1!");
            }
            if (quantity > 10) {
                return ResponseEntity.badRequest()
                        .body("Maximum 10 items allowed per product!");
            }

            String email = getEmail();
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

    // ✅ T037 - Update Cart Quantity
    // PUT http://localhost:8080/api/cart/update/1
    // Body: { "quantity": 3 }
    @PutMapping("/update/{cartId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long cartId,
            @RequestBody Map<String, Object> request) {
        try {
            // T042 - Validate request
            if (request.get("quantity") == null) {
                return ResponseEntity.badRequest()
                        .body("Quantity is required!");
            }

            int quantity = Integer.parseInt(
                    request.get("quantity").toString()
            );

            // T042 - Validate quantity range
            if (quantity > 10) {
                return ResponseEntity.badRequest()
                        .body("Maximum 10 items allowed per product!");
            }

            Cart updated = cartService.updateCartItem(
                    cartId, quantity
            );

            if (updated == null) {
                return ResponseEntity.ok(
                        "Item removed because quantity was 0!"
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
        try {
            String email = getEmail();
            cartService.clearCart(email);
            return ResponseEntity.ok("Cart cleared successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // BONUS - Get cart total
    // GET http://localhost:8080/api/cart/total
    @GetMapping("/total")
    public ResponseEntity<?> getTotal() {
        try {
            String email = getEmail();
            Double total = cartService.getCartTotal(email);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // BONUS - Get cart count
    // GET http://localhost:8080/api/cart/count
    @GetMapping("/count")
    public ResponseEntity<?> getCount() {
        try {
            String email = getEmail();
            int count = cartService.getCartCount(email);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}