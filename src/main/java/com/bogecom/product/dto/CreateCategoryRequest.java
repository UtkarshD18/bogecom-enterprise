package com.bogecom.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
    @NotBlank(message = "Name is required") String name, String description, Long parentId) {}
