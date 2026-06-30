package com.bogecom.inventory.dto;

public record InventoryDto(
    Long id,
    Long productId,
    String location,
    Integer availableQuantity,
    Integer reservedQuantity,
    Integer totalQuantity) {}
