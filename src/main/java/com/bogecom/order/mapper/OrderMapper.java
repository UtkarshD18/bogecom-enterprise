package com.bogecom.order.mapper;

import com.bogecom.order.dto.OrderDto;
import com.bogecom.order.dto.OrderItemDto;
import com.bogecom.order.entity.Order;
import com.bogecom.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    builder = @org.mapstruct.Builder(disableBuilder = true))
public interface OrderMapper {

  OrderDto toDto(Order order);

  @Mapping(source = "order.id", target = "orderId")
  OrderItemDto toDto(OrderItem orderItem);
}
