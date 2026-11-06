package com.pcconfigurations.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreedyBuildResponse {
    private BigDecimal budget;
    private BigDecimal totalPrice;
    private BigDecimal remainingBudget;
    private List<ProductDTO> selectedComponents;
    private String strategy;
    private String message;
}
