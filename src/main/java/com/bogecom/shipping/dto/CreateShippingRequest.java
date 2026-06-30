package com.bogecom.shipping.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateShippingRequest(
    @NotNull(message = "Order ID is required") Long orderId,
    @NotNull(message = "Address ID is required") Long addressId,
    @NotNull(message = "Cost is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Cost cannot be negative")
        BigDecimal cost) {}
