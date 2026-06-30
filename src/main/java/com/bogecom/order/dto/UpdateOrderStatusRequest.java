package com.bogecom.order.dto;

import com.bogecom.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull(message = "Order status is required") OrderStatus status) {}
