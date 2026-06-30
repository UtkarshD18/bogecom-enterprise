package com.bogecom.product.dto;

public record ProductImageDto(
        Long id,
        String url,
        boolean isPrimary,
        Integer sortOrder
) {}
