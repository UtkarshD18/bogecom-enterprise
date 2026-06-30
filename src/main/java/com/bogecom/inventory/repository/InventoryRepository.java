package com.bogecom.inventory.repository;

import com.bogecom.inventory.entity.Inventory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProductIdAndIsDeletedFalse(Long productId);

    Optional<Inventory> findByProductIdAndLocationAndIsDeletedFalse(Long productId, String location);
    
    boolean existsByProductIdAndLocationAndIsDeletedFalse(Long productId, String location);
}
