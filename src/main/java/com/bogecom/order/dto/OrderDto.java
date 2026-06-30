package com.bogecom.order.dto;

import com.bogecom.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
    Long id,
    Long userId,
    OrderStatus status,
    BigDecimal totalAmount,
    Long shippingAddressId,
    Long billingAddressId,
    LocalDateTime createdAt,
    List<OrderItemDto> items) {}
