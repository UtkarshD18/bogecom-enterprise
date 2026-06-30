package com.bogecom.loyalty.service;

import com.bogecom.exception.BusinessException;
import com.bogecom.loyalty.dto.AddCoinsRequest;
import com.bogecom.loyalty.dto.CoinWalletDto;
import com.bogecom.loyalty.dto.DeductCoinsRequest;
import com.bogecom.loyalty.entity.CoinTransaction;
import com.bogecom.loyalty.entity.CoinWallet;
import com.bogecom.loyalty.mapper.LoyaltyMapper;
import com.bogecom.loyalty.repository.CoinWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyService {

  private final CoinWalletRepository walletRepository;
  private final LoyaltyMapper loyaltyMapper;

  @Transactional(readOnly = true)
  public CoinWalletDto getWallet(Long userId) {
    return loyaltyMapper.toDto(getOrCreateWallet(userId));
  }

  @Transactional
  public CoinWalletDto addCoins(Long userId, AddCoinsRequest request) {
    CoinWallet wallet = getOrCreateWallet(userId);

    CoinTransaction transaction =
        CoinTransaction.builder()
            .amount(request.amount())
            .type(request.type())
            .referenceId(request.referenceId())
            .description(request.description())
            .build();

    wallet.addTransaction(transaction);
    wallet.setBalance(wallet.getBalance() + request.amount());

    wallet = walletRepository.save(wallet);

    log.info("Added {} coins to wallet for user {}", request.amount(), userId);
    return loyaltyMapper.toDto(wallet);
  }

  @Transactional
  public CoinWalletDto deductCoins(Long userId, DeductCoinsRequest request) {
    CoinWallet wallet = getOrCreateWallet(userId);

    if (wallet.getBalance() < request.amount()) {
      throw new BusinessException("Insufficient coin balance", HttpStatus.BAD_REQUEST);
    }

    CoinTransaction transaction =
        CoinTransaction.builder()
            .amount(-request.amount())
            .type(request.type())
            .referenceId(request.referenceId())
            .description(request.description())
            .build();

    wallet.addTransaction(transaction);
    wallet.setBalance(wallet.getBalance() - request.amount());

    wallet = walletRepository.save(wallet);

    log.info("Deducted {} coins from wallet for user {}", request.amount(), userId);
    return loyaltyMapper.toDto(wallet);
  }

  private CoinWallet getOrCreateWallet(Long userId) {
    return walletRepository
        .findByUserIdAndIsDeletedFalse(userId)
        .orElseGet(
            () -> {
              CoinWallet newWallet = CoinWallet.builder().userId(userId).balance(0L).build();
              return walletRepository.save(newWallet);
            });
  }
}
