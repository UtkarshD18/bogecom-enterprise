package com.bogecom.loyalty.dto;

import java.util.List;

public record CoinWalletDto(
    Long id, Long userId, Long balance, List<CoinTransactionDto> transactions) {}
