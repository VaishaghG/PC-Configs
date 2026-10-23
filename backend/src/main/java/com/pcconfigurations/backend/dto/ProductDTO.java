package com.pcconfigurations.backend.dto;

import com.pcconfigurations.backend.entity.Product;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String brand;
    private String description;
    private Product.Category category;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String specifications;

    public static ProductDTO fromProduct(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setBrand(p.getBrand());
        dto.setDescription(p.getDescription());
        dto.setCategory(p.getCategory());
        dto.setPrice(p.getPrice());
        dto.setStockQuantity(p.getStockQuantity());
        dto.setImageUrl(p.getImageUrl());
        dto.setSpecifications(p.getSpecifications());
        return dto;
    }
}
