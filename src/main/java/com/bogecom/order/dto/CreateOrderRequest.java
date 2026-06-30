package com.bogecom.order.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull(message = "Cart ID is required") Long cartId,
    @NotNull(message = "Shipping address ID is required") Long shippingAddressId,
    @NotNull(message = "Billing address ID is required") Long billingAddressId) {}
