package com.pcconfigurations.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private List<CartItemDTO> items;
    private BigDecimal total;
    private int itemCount;
}
