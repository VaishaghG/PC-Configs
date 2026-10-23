package com.pcconfigurations.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PcBuildDTO {
    private Long id;
    private String buildName;
    private String notes;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ComponentDTO> components;

    @Data
    public static class ComponentDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productBrand;
        private String componentType;
        private BigDecimal price;
        private String imageUrl;
    }
}
