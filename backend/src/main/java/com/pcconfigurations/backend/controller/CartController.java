package com.pcconfigurations.backend.controller;

import com.pcconfigurations.backend.dto.CartDTO;
import com.pcconfigurations.backend.entity.User;
import com.pcconfigurations.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        try {
            Long productId = Long.valueOf(body.get("productId").toString());
            int quantity = body.containsKey("quantity") ?
                    Integer.parseInt(body.get("quantity").toString()) : 1;
            return ResponseEntity.ok(cartService.addToCart(user, productId, quantity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        try {
            return ResponseEntity.ok(cartService.updateQuantity(user, itemId, body.get("quantity")));
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long itemId) {
        try {
            return ResponseEntity.ok(cartService.removeItem(user, itemId));
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }
}
