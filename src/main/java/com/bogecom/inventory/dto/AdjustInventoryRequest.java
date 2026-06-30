package com.bogecom.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdjustInventoryRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "Location is required")
        String location,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Available quantity cannot be negative")
        Integer availableQuantity
) {}
