package com.bogecom.order.controller;

import com.bogecom.common.response.ApiResponse;
import com.bogecom.order.dto.CreateOrderRequest;
import com.bogecom.order.dto.OrderDto;
import com.bogecom.order.dto.UpdateOrderStatusRequest;
import com.bogecom.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<ApiResponse<OrderDto>> createOrder(
      @AuthenticationPrincipal Long userId, @Valid @RequestBody CreateOrderRequest request) {

    return ResponseEntity.ok(ApiResponse.success(orderService.createOrder(userId, request)));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderDto>>> getUserOrders(
      @AuthenticationPrincipal Long userId) {

    return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(userId)));
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<ApiResponse<OrderDto>> getOrder(@PathVariable Long orderId) {

    return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(orderId)));
  }

  @PutMapping("/{orderId}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
      @PathVariable Long orderId, @Valid @RequestBody UpdateOrderStatusRequest request) {

    return ResponseEntity.ok(ApiResponse.success(orderService.updateOrderStatus(orderId, request)));
  }
}
