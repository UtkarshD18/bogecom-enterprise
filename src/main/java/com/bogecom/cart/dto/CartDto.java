package com.bogecom.cart.dto;

import com.bogecom.cart.entity.CartStatus;
import java.util.List;

public record CartDto(
        Long id,
        Long userId,
        String sessionId,
        CartStatus status,
        List<CartItemDto> items
) {}
