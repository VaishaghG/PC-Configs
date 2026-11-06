package com.pcconfigurations.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GreedyBuildRequest {

    @NotNull(message = "Budget cannot be empty")
    @DecimalMin(value = "1.0", message = "Budget must be greater than zero")
    private BigDecimal budget;
}
