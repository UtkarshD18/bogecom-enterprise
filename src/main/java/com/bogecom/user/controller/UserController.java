package com.bogecom.user.controller;

import com.bogecom.common.response.ApiResponse;
import com.bogecom.user.dto.AddressDto;
import com.bogecom.user.dto.AdminUpdateUserRequest;
import com.bogecom.user.dto.UpdateProfileRequest;
import com.bogecom.user.dto.UserResponse;
import com.bogecom.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // --- Profile & Address Endpoints (Self) ---

  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
      @AuthenticationPrincipal Long userId, @Valid @RequestBody UpdateProfileRequest request) {
    return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(userId, request)));
  }

  @GetMapping("/me/addresses")
  public ResponseEntity<ApiResponse<List<AddressDto>>> getMyAddresses(
      @AuthenticationPrincipal Long userId) {
    return ResponseEntity.ok(ApiResponse.success(userService.getUserAddresses(userId)));
  }

  @PostMapping("/me/addresses")
  public ResponseEntity<ApiResponse<AddressDto>> addAddress(
      @AuthenticationPrincipal Long userId, @Valid @RequestBody AddressDto request) {
    return ResponseEntity.ok(ApiResponse.success(userService.addAddress(userId, request)));
  }

  @PutMapping("/me/addresses/{id}")
  public ResponseEntity<ApiResponse<AddressDto>> updateAddress(
      @AuthenticationPrincipal Long userId,
      @PathVariable Long id,
      @Valid @RequestBody AddressDto request) {
    return ResponseEntity.ok(ApiResponse.success(userService.updateAddress(userId, id, request)));
  }

  @DeleteMapping("/me/addresses/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteAddress(
      @AuthenticationPrincipal Long userId, @PathVariable Long id) {
    userService.deleteAddress(userId, id);
    return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
  }

  // --- Admin Endpoints ---

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
    return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<UserResponse>> adminUpdateUser(
      @PathVariable Long id, @Valid @RequestBody AdminUpdateUserRequest request) {
    return ResponseEntity.ok(ApiResponse.success(userService.adminUpdateUser(id, request)));
  }
}
