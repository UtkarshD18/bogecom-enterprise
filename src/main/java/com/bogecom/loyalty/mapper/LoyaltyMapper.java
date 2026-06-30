package com.bogecom.loyalty.mapper;

import com.bogecom.loyalty.dto.CoinTransactionDto;
import com.bogecom.loyalty.dto.CoinWalletDto;
import com.bogecom.loyalty.entity.CoinTransaction;
import com.bogecom.loyalty.entity.CoinWallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    builder = @org.mapstruct.Builder(disableBuilder = true))
public interface LoyaltyMapper {

  CoinWalletDto toDto(CoinWallet wallet);

  @Mapping(source = "transaction.wallet.id", target = "walletId")
  CoinTransactionDto toDto(CoinTransaction transaction);
}
