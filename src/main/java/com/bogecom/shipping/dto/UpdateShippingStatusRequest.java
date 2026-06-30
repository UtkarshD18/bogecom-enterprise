package com.bogecom.shipping.dto;

import com.bogecom.shipping.entity.ShippingStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UpdateShippingStatusRequest(
    @NotNull(message = "Status is required") ShippingStatus status,
    String trackingNumber,
    String carrier,
    LocalDateTime estimatedDeliveryDate) {}
