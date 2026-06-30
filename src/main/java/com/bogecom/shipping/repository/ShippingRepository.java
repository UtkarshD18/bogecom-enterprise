package com.bogecom.shipping.repository;

import com.bogecom.shipping.entity.Shipping;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

  Optional<Shipping> findByOrderIdAndIsDeletedFalse(Long orderId);
}
