package com.bogecom.shipping.service;

import com.bogecom.exception.BusinessException;
import com.bogecom.shipping.dto.CreateShippingRequest;
import com.bogecom.shipping.dto.ShippingDto;
import com.bogecom.shipping.dto.UpdateShippingStatusRequest;
import com.bogecom.shipping.entity.Shipping;
import com.bogecom.shipping.entity.ShippingStatus;
import com.bogecom.shipping.mapper.ShippingMapper;
import com.bogecom.shipping.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

  private final ShippingRepository shippingRepository;
  private final ShippingMapper shippingMapper;

  @Transactional
  public ShippingDto createShipping(CreateShippingRequest request) {
    if (shippingRepository.findByOrderIdAndIsDeletedFalse(request.orderId()).isPresent()) {
      throw new BusinessException(
          "Shipping record already exists for this order", HttpStatus.BAD_REQUEST);
    }

    Shipping shipping =
        Shipping.builder()
            .orderId(request.orderId())
            .addressId(request.addressId())
            .status(ShippingStatus.PENDING)
            .cost(request.cost())
            .build();

    shipping = shippingRepository.save(shipping);
    log.info("Created shipping record {} for order {}", shipping.getId(), request.orderId());
    return shippingMapper.toDto(shipping);
  }

  @Transactional(readOnly = true)
  public ShippingDto getShippingByOrderId(Long orderId) {
    Shipping shipping =
        shippingRepository
            .findByOrderIdAndIsDeletedFalse(orderId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "Shipping record not found for order", HttpStatus.NOT_FOUND));
    return shippingMapper.toDto(shipping);
  }

  @Transactional
  public ShippingDto updateShippingStatus(Long shippingId, UpdateShippingStatusRequest request) {
    Shipping shipping =
        shippingRepository
            .findById(shippingId)
            .filter(s -> !s.isDeleted())
            .orElseThrow(
                () -> new BusinessException("Shipping record not found", HttpStatus.NOT_FOUND));

    shipping.setStatus(request.status());

    if (request.trackingNumber() != null) {
      shipping.setTrackingNumber(request.trackingNumber());
    }

    if (request.carrier() != null) {
      shipping.setCarrier(request.carrier());
    }

    if (request.estimatedDeliveryDate() != null) {
      shipping.setEstimatedDeliveryDate(request.estimatedDeliveryDate());
    }

    shipping = shippingRepository.save(shipping);
    log.info("Updated status of shipping {} to {}", shipping.getId(), request.status());
    return shippingMapper.toDto(shipping);
  }
}
