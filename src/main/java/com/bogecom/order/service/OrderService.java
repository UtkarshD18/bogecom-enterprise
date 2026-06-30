package com.bogecom.order.service;

import com.bogecom.cart.entity.Cart;
import com.bogecom.cart.entity.CartItem;
import com.bogecom.cart.entity.CartStatus;
import com.bogecom.cart.repository.CartRepository;
import com.bogecom.exception.BusinessException;
import com.bogecom.inventory.dto.ReserveInventoryRequest;
import com.bogecom.inventory.service.InventoryService;
import com.bogecom.order.dto.CreateOrderRequest;
import com.bogecom.order.dto.OrderDto;
import com.bogecom.order.dto.UpdateOrderStatusRequest;
import com.bogecom.order.entity.Order;
import com.bogecom.order.entity.OrderItem;
import com.bogecom.order.entity.OrderStatus;
import com.bogecom.order.mapper.OrderMapper;
import com.bogecom.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final CartRepository
      cartRepository; // Directly accessing to avoid circular bean issues if not careful, though
  // CartFacade is better
  private final InventoryService inventoryService;
  private final OrderMapper orderMapper;

  @Transactional
  public OrderDto createOrder(Long userId, CreateOrderRequest request) {
    Cart cart =
        cartRepository
            .findById(request.cartId())
            .filter(c -> !c.isDeleted())
            .orElseThrow(() -> new BusinessException("Cart not found", HttpStatus.NOT_FOUND));

    if (!userId.equals(cart.getUserId())) {
      throw new BusinessException("Cart does not belong to user", HttpStatus.FORBIDDEN);
    }

    if (cart.getStatus() != CartStatus.ACTIVE) {
      throw new BusinessException("Cart is no longer active", HttpStatus.BAD_REQUEST);
    }

    if (cart.getItems().isEmpty()) {
      throw new BusinessException("Cannot create order from an empty cart", HttpStatus.BAD_REQUEST);
    }

    BigDecimal totalAmount = BigDecimal.ZERO;
    for (CartItem item : cart.getItems()) {
      totalAmount =
          totalAmount.add(item.getPriceAtAdded().multiply(BigDecimal.valueOf(item.getQuantity())));
    }

    Order order = new Order();
    order.setUserId(userId);
    order.setStatus(OrderStatus.PENDING);
    order.setTotalAmount(totalAmount);
    order.setShippingAddressId(request.shippingAddressId());
    order.setBillingAddressId(request.billingAddressId());

    for (CartItem cartItem : cart.getItems()) {
      OrderItem orderItem =
          OrderItem.builder()
              .productId(cartItem.getProductId())
              .quantity(cartItem.getQuantity())
              .price(cartItem.getPriceAtAdded())
              .build();
      order.addItem(orderItem);
    }

    order = orderRepository.save(order);

    // Reserve inventory for each item
    for (OrderItem item : order.getItems()) {
      ReserveInventoryRequest reserveRequest =
          new ReserveInventoryRequest(
              item.getProductId(), cart.getId(), order.getId(), item.getQuantity());
      inventoryService.reserveInventory(reserveRequest);
    }

    // Convert cart
    cart.setStatus(CartStatus.CONVERTED);
    cartRepository.save(cart);

    log.info("Created order {} for user {} from cart {}", order.getId(), userId, cart.getId());
    return orderMapper.toDto(order);
  }

  @Transactional(readOnly = true)
  public List<OrderDto> getUserOrders(Long userId) {
    return orderRepository.findByUserIdAndIsDeletedFalse(userId).stream()
        .map(orderMapper::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public OrderDto getOrder(Long orderId) {
    return orderMapper.toDto(getOrderEntity(orderId));
  }

  @Transactional
  public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
    Order order = getOrderEntity(orderId);
    order.setStatus(request.status());
    order = orderRepository.save(order);
    log.info("Updated status of order {} to {}", order.getId(), request.status());
    return orderMapper.toDto(order);
  }

  private Order getOrderEntity(Long id) {
    return orderRepository
        .findById(id)
        .filter(o -> !o.isDeleted())
        .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));
  }
}
