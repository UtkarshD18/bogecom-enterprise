package com.bogecom.shipping.entity;

import com.bogecom.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipping")
public class Shipping extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Direct linking for monolith, but by ID to keep modules loosely coupled
  @Column(name = "order_id", nullable = false, unique = true)
  private Long orderId;

  @Column(name = "address_id", nullable = false)
  private Long addressId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private ShippingStatus status;

  @Column(name = "tracking_number", length = 100)
  private String trackingNumber;

  @Column(length = 100)
  private String carrier;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cost;

  @Column(name = "estimated_delivery_date")
  private LocalDateTime estimatedDeliveryDate;
}
