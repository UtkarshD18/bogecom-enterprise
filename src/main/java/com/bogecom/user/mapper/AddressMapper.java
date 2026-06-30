package com.bogecom.user.mapper;

import com.bogecom.user.dto.AddressDto;
import com.bogecom.user.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    builder = @org.mapstruct.Builder(disableBuilder = true))
public interface AddressMapper {

  AddressDto toDto(Address address);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  @Mapping(target = "version", ignore = true)
  Address toEntity(AddressDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  @Mapping(target = "version", ignore = true)
  void updateEntityFromDto(AddressDto dto, @MappingTarget Address address);
}
