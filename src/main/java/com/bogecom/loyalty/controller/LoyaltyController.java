package com.bogecom.loyalty.controller;

import com.bogecom.common.response.ApiResponse;
import com.bogecom.loyalty.dto.AddCoinsRequest;
import com.bogecom.loyalty.dto.CoinWalletDto;
import com.bogecom.loyalty.dto.DeductCoinsRequest;
import com.bogecom.loyalty.service.LoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loyalty/wallet")
@RequiredArgsConstructor
public class LoyaltyController {

  private final LoyaltyService loyaltyService;

  @GetMapping
  public ResponseEntity<ApiResponse<CoinWalletDto>> getWallet(
      @AuthenticationPrincipal Long userId) {

    return ResponseEntity.ok(ApiResponse.success(loyaltyService.getWallet(userId)));
  }

  @PostMapping("/add")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<CoinWalletDto>> addCoins(
      @AuthenticationPrincipal Long userId, @Valid @RequestBody AddCoinsRequest request) {

    // In a real scenario, the userId path parameter would be needed for an admin to add coins to an
    // arbitrary user.
    // For simplicity, we are assuming this adds coins to the currently logged-in user, but guarded
    // by ADMIN.
    return ResponseEntity.ok(ApiResponse.success(loyaltyService.addCoins(userId, request)));
  }

  @PostMapping("/deduct")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<CoinWalletDto>> deductCoins(
      @AuthenticationPrincipal Long userId, @Valid @RequestBody DeductCoinsRequest request) {

    return ResponseEntity.ok(ApiResponse.success(loyaltyService.deductCoins(userId, request)));
  }
}
