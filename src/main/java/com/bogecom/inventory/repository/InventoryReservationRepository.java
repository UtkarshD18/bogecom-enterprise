package com.bogecom.inventory.repository;

import com.bogecom.inventory.entity.InventoryReservation;
import com.bogecom.inventory.entity.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

    List<InventoryReservation> findByOrderIdAndIsDeletedFalse(Long orderId);

    List<InventoryReservation> findByCartIdAndIsDeletedFalse(Long cartId);

    List<InventoryReservation> findByStatusAndExpiresAtBeforeAndIsDeletedFalse(ReservationStatus status, LocalDateTime now);
}
