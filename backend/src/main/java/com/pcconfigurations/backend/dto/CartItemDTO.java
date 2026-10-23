package com.pcconfigurations.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productBrand;
    private String productCategory;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String imageUrl;
    private Integer stockQuantity;
}
