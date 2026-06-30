package com.bogecom.inventory.controller;

import com.bogecom.common.response.ApiResponse;
import com.bogecom.inventory.dto.AdjustInventoryRequest;
import com.bogecom.inventory.dto.InventoryDto;
import com.bogecom.inventory.dto.InventoryReservationDto;
import com.bogecom.inventory.dto.ReserveInventoryRequest;
import com.bogecom.inventory.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

  private final InventoryService inventoryService;

  @GetMapping("/products/{productId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<List<InventoryDto>>> getInventoryForProduct(
      @PathVariable Long productId) {
    return ResponseEntity.ok(
        ApiResponse.success(inventoryService.getInventoryForProduct(productId)));
  }

  @PostMapping("/adjust")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<InventoryDto>> adjustInventory(
      @Valid @RequestBody AdjustInventoryRequest request) {
    return ResponseEntity.ok(ApiResponse.success(inventoryService.adjustInventory(request)));
  }

  // Usually reservations happen internally during checkout, but exposing an endpoint is good for
  // microservice extraction
  @PostMapping("/reserve")
  @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<InventoryReservationDto>> reserveInventory(
      @Valid @RequestBody ReserveInventoryRequest request) {
    return ResponseEntity.ok(ApiResponse.success(inventoryService.reserveInventory(request)));
  }
}
