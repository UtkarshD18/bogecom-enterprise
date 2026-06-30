package com.bogecom.product.dto;

import java.math.BigDecimal;

public record ProductSearchCriteria(
    String keyword, BigDecimal minPrice, BigDecimal maxPrice, Boolean isPublished) {}
