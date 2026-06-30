package com.bogecom.product.dto;

import java.util.List;

public record CategoryDto(
        Long id,
        String name,
        String slug,
        String description,
        Long parentId,
        boolean isActive,
        List<CategoryDto> subCategories
) {}
