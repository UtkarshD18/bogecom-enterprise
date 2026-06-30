package com.bogecom.loyalty.dto;

import com.bogecom.loyalty.entity.CoinTransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCoinsRequest(
    @NotNull(message = "Amount is required") @Min(value = 1, message = "Amount must be at least 1")
        Long amount,
    @NotNull(message = "Transaction type is required") CoinTransactionType type,
    Long referenceId,
    String description) {}
