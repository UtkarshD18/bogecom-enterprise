package com.bogecom.cart.controller;

import com.bogecom.cart.dto.AddToCartRequest;
import com.bogecom.cart.dto.CartDto;
import com.bogecom.cart.dto.UpdateCartItemRequest;
import com.bogecom.cart.service.CartService;
import com.bogecom.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping
  public ResponseEntity<ApiResponse<CartDto>> getCart(
      @AuthenticationPrincipal Long userId, @RequestParam(required = false) String sessionId) {

    return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userId, sessionId)));
  }

  @PostMapping("/items")
  public ResponseEntity<ApiResponse<CartDto>> addToCart(
      @AuthenticationPrincipal Long userId, @Valid @RequestBody AddToCartRequest request) {

    return ResponseEntity.ok(ApiResponse.success(cartService.addToCart(userId, request)));
  }

  @PutMapping("/items/{itemId}")
  public ResponseEntity<ApiResponse<CartDto>> updateCartItem(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long itemId,
      @Valid @RequestBody UpdateCartItemRequest request) {

    return ResponseEntity.ok(
        ApiResponse.success(cartService.updateCartItem(userId, itemId, request)));
  }

  @DeleteMapping("/items/{itemId}")
  public ResponseEntity<ApiResponse<CartDto>> removeCartItem(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long itemId,
      @RequestParam(required = false) String sessionId) {

    return ResponseEntity.ok(
        ApiResponse.success(cartService.removeCartItem(userId, sessionId, itemId)));
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> clearCart(
      @AuthenticationPrincipal Long userId, @RequestParam(required = false) String sessionId) {

    cartService.clearCart(userId, sessionId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("/merge")
  public ResponseEntity<ApiResponse<Void>> mergeCart(
      @AuthenticationPrincipal Long userId, @RequestParam String sessionId) {

    if (userId == null) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("User must be logged in to merge carts"));
    }

    cartService.mergeGuestCartWithUserCart(userId, sessionId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
