package com.bogecom.user.dto;

import com.bogecom.user.entity.Role;
import java.time.LocalDateTime;

public record UserResponse(
    Long id, String email, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {}
