package com.bogecom.shipping.mapper;

import com.bogecom.shipping.dto.ShippingDto;
import com.bogecom.shipping.entity.Shipping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    builder = @org.mapstruct.Builder(disableBuilder = true))
public interface ShippingMapper {

  ShippingDto toDto(Shipping shipping);
}
