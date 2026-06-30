package com.bogecom.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressDto(
    Long id,
    @NotBlank(message = "Street Line 1 is required") @Size(max = 255) String streetLine1,
    @Size(max = 255) String streetLine2,
    @NotBlank(message = "City is required") @Size(max = 100) String city,
    @NotBlank(message = "State is required") @Size(max = 100) String state,
    @NotBlank(message = "Postal code is required") @Size(max = 20) String postalCode,
    @NotBlank(message = "Country is required") @Size(max = 100) String country,
    boolean isDefault) {}
