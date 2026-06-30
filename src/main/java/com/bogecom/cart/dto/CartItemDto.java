package com.bogecom.cart.dto;

import java.math.BigDecimal;

public record CartItemDto(
    Long id, Long cartId, Long productId, Integer quantity, BigDecimal priceAtAdded) {}
