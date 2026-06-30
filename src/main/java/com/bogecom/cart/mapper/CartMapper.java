package com.bogecom.cart.mapper;

import com.bogecom.cart.dto.CartDto;
import com.bogecom.cart.dto.CartItemDto;
import com.bogecom.cart.entity.Cart;
import com.bogecom.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    builder = @org.mapstruct.Builder(disableBuilder = true))
public interface CartMapper {

  CartDto toDto(Cart cart);

  @Mapping(source = "cart.id", target = "cartId")
  CartItemDto toDto(CartItem cartItem);
}
