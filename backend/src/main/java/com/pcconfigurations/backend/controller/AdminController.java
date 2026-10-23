package com.pcconfigurations.backend.controller;

import com.pcconfigurations.backend.dto.OrderDTO;
import com.pcconfigurations.backend.dto.ProductDTO;
import com.pcconfigurations.backend.entity.Order;
import com.pcconfigurations.backend.entity.Product;
import com.pcconfigurations.backend.repository.OrderRepository;
import com.pcconfigurations.backend.repository.ProductRepository;
import com.pcconfigurations.backend.repository.UserRepository;
import com.pcconfigurations.backend.service.OrderService;
import com.pcconfigurations.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        List<Order> allOrders = orderRepository.findAll();
        List<Product> allProducts = productRepository.findAll();
        long totalUsers = userRepository.count();

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PENDING || o.getStatus() == Order.OrderStatus.CONFIRMED || o.getStatus() == Order.OrderStatus.PROCESSING)
                .count();

        long deliveredOrders = allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .count();

        long lowStockCount = allProducts.stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() < 5)
                .count();

        // Category distribution
        Map<String, Long> categoryCount = allProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory().name(), Collectors.counting()));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalOrders", allOrders.size());
        stats.put("totalProducts", allProducts.size());
        stats.put("totalUsers", totalUsers);
        stats.put("pendingOrders", pendingOrders);
        stats.put("deliveredOrders", deliveredOrders);
        stats.put("lowStockCount", lowStockCount);
        stats.put("categoryCount", categoryCount);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            if (statusStr == null || statusStr.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            return ResponseEntity.ok(orderService.updateOrderStatus(id, newStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, product));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
