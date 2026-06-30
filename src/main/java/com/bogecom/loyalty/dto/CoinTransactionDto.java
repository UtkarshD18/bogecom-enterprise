package com.bogecom.loyalty.dto;

import com.bogecom.loyalty.entity.CoinTransactionType;
import java.time.LocalDateTime;

public record CoinTransactionDto(
    Long id,
    Long walletId,
    Long amount,
    CoinTransactionType type,
    Long referenceId,
    String description,
    LocalDateTime createdAt) {}
