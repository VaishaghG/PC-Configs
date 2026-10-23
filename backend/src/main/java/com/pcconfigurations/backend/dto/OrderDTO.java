package com.pcconfigurations.backend.dto;

import com.pcconfigurations.backend.entity.Order;
import com.pcconfigurations.backend.entity.Payment;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items;
    private String paymentMethod;
    private String paymentStatus;

    @Data
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productBrand;
        private String productCategory;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private String imageUrl;
    }
}
