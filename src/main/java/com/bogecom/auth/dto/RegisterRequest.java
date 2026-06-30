package com.bogecom.auth.dto;

import com.bogecom.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
    @NotBlank(message = "Password is required")
        @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_]).{8,}$",
            message =
                "Password must be at least 8 characters long, contain 1 uppercase, 1 lowercase, 1 number, and 1 special character")
        String password,
    @NotNull(message = "Role is required") Role role) {}
