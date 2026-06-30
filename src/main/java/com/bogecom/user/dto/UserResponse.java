package com.bogecom.user.dto;

import com.bogecom.user.entity.Role;
import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    String phone,
    Role role,
    com.bogecom.user.entity.AccountStatus accountStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
