package com.bogecom.order.dto;

import java.math.BigDecimal;

public record OrderItemDto(
    Long id, Long orderId, Long productId, Integer quantity, BigDecimal price) {}
