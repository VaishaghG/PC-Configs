package com.pcconfigurations.backend.dto;

import com.pcconfigurations.backend.entity.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod;

    // Optional: if ordering specific items rather than full cart
    private List<Long> cartItemIds;
}
