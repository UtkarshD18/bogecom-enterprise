package com.bogecom.inventory.dto;

import com.bogecom.inventory.entity.ReservationStatus;
import java.time.LocalDateTime;

public record InventoryReservationDto(
    Long id,
    Long inventoryId,
    Long orderId,
    Long cartId,
    Integer quantity,
    ReservationStatus status,
    LocalDateTime expiresAt) {}
