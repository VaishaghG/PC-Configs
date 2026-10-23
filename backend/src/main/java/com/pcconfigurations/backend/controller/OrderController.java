package com.pcconfigurations.backend.controller;

import com.pcconfigurations.backend.dto.OrderDTO;
import com.pcconfigurations.backend.dto.OrderRequest;
import com.pcconfigurations.backend.entity.User;
import com.pcconfigurations.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody OrderRequest request) {
        try {
            OrderDTO order = orderService.createOrder(user, request);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(user, id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
    }
}
