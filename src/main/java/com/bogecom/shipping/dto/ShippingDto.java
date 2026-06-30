package com.bogecom.shipping.dto;

import com.bogecom.shipping.entity.ShippingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShippingDto(
    Long id,
    Long orderId,
    Long addressId,
    ShippingStatus status,
    String trackingNumber,
    String carrier,
    BigDecimal cost,
    LocalDateTime estimatedDeliveryDate,
    LocalDateTime createdAt) {}
