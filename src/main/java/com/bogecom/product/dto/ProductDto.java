package com.bogecom.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto(
    Long id,
    String sku,
    String name,
    String slug,
    String description,
    BigDecimal price,
    BigDecimal salePrice,
    Integer stockQuantity,
    boolean isPublished,
    List<ProductImageDto> images) {}
