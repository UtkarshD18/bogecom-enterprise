package com.bogecom.auth.dto;

import com.bogecom.user.dto.UserResponse;

public record AuthResponse(UserResponse user, String message) {}
