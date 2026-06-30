package com.bogecom.user.mapper;

import com.bogecom.user.dto.UserResponse;
import com.bogecom.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

  UserResponse toResponse(User user);
}
