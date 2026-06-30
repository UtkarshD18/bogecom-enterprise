package com.bogecom.user.dto;

import com.bogecom.user.entity.AccountStatus;
import com.bogecom.user.entity.Role;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateUserRequest(
    @NotNull(message = "Role must be provided") Role role,
    @NotNull(message = "Account status must be provided") AccountStatus accountStatus) {}
