package com.bogecom.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReserveInventoryRequest(
    @NotNull(message = "Product ID is required") Long productId,
    Long cartId,
    Long orderId,
    @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Reservation quantity must be at least 1")
        Integer quantity) {}
