package com.bogecom.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateProductRequest(
    @NotBlank(message = "Name is required") String name,
    @NotBlank(message = "Description is required") String description,
    @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        BigDecimal price,
    @DecimalMin(value = "0.0", inclusive = false, message = "Sale price must be greater than zero")
        BigDecimal salePrice,
    @NotNull(message = "Stock quantity is required") Integer stockQuantity,
    boolean isPublished) {}
