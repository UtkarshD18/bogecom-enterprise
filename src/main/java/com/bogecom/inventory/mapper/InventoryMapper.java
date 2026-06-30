package com.bogecom.inventory.mapper;

import com.bogecom.inventory.dto.InventoryDto;
import com.bogecom.inventory.dto.InventoryReservationDto;
import com.bogecom.inventory.entity.Inventory;
import com.bogecom.inventory.entity.InventoryReservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, builder = @org.mapstruct.Builder(disableBuilder = true))
public interface InventoryMapper {

    @Mapping(source = "product.id", target = "productId")
    InventoryDto toDto(Inventory inventory);

    @Mapping(source = "inventory.id", target = "inventoryId")
    InventoryReservationDto toDto(InventoryReservation reservation);
}
