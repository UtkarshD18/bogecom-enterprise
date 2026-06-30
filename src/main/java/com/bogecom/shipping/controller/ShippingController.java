package com.bogecom.shipping.controller;

import com.bogecom.common.response.ApiResponse;
import com.bogecom.shipping.dto.CreateShippingRequest;
import com.bogecom.shipping.dto.ShippingDto;
import com.bogecom.shipping.dto.UpdateShippingStatusRequest;
import com.bogecom.shipping.service.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

  private final ShippingService shippingService;

  @PostMapping
  @PreAuthorize(
      "hasAnyRole('ADMIN', 'MANAGER')") // Typically internal systems create shipping records
  public ResponseEntity<ApiResponse<ShippingDto>> createShipping(
      @Valid @RequestBody CreateShippingRequest request) {

    return ResponseEntity.ok(ApiResponse.success(shippingService.createShipping(request)));
  }

  @GetMapping("/orders/{orderId}")
  public ResponseEntity<ApiResponse<ShippingDto>> getShippingByOrderId(@PathVariable Long orderId) {

    // In a real app, verify that the logged-in user owns the order, but keeping it simple
    return ResponseEntity.ok(ApiResponse.success(shippingService.getShippingByOrderId(orderId)));
  }

  @PutMapping("/{shippingId}/status")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public ResponseEntity<ApiResponse<ShippingDto>> updateShippingStatus(
      @PathVariable Long shippingId, @Valid @RequestBody UpdateShippingStatusRequest request) {

    return ResponseEntity.ok(
        ApiResponse.success(shippingService.updateShippingStatus(shippingId, request)));
  }
}
