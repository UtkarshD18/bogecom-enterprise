package com.bogecom.inventory.service;

import com.bogecom.exception.BusinessException;
import com.bogecom.inventory.dto.AdjustInventoryRequest;
import com.bogecom.inventory.dto.InventoryDto;
import com.bogecom.inventory.dto.InventoryReservationDto;
import com.bogecom.inventory.dto.ReserveInventoryRequest;
import com.bogecom.inventory.entity.Inventory;
import com.bogecom.inventory.entity.InventoryReservation;
import com.bogecom.inventory.entity.ReservationStatus;
import com.bogecom.inventory.mapper.InventoryMapper;
import com.bogecom.inventory.repository.InventoryRepository;
import com.bogecom.inventory.repository.InventoryReservationRepository;
import com.bogecom.product.entity.Product;
import com.bogecom.product.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    @Transactional(readOnly = true)
    public List<InventoryDto> getInventoryForProduct(Long productId) {
        return inventoryRepository.findByProductIdAndIsDeletedFalse(productId).stream()
                .map(inventoryMapper::toDto)
                .toList();
    }

    @Transactional
    public InventoryDto adjustInventory(AdjustInventoryRequest request) {
        Product product = getProductEntityById(request.productId());

        Inventory inventory = inventoryRepository.findByProductIdAndLocationAndIsDeletedFalse(
                        request.productId(), request.location())
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setProduct(product);
                    newInventory.setLocation(request.location());
                    newInventory.setAvailableQuantity(0);
                    newInventory.setReservedQuantity(0);
                    return newInventory;
                });

        inventory.setAvailableQuantity(request.availableQuantity());
        inventory = inventoryRepository.save(inventory);

        syncProductStockQuantity(product);

        log.info("Adjusted inventory for product {} at {} to {}", request.productId(), request.location(), request.availableQuantity());
        return inventoryMapper.toDto(inventory);
    }

    @Transactional
    public InventoryReservationDto reserveInventory(ReserveInventoryRequest request) {
        if (request.cartId() == null && request.orderId() == null) {
            throw new BusinessException("Either cartId or orderId must be provided", HttpStatus.BAD_REQUEST);
        }

        Product product = getProductEntityById(request.productId());
        
        // Find an inventory location with enough available stock
        // For simplicity, we just find the first one that has enough stock.
        // Enterprise systems usually have complex routing logic.
        List<Inventory> inventories = inventoryRepository.findByProductIdAndIsDeletedFalse(request.productId());
        
        Inventory selectedInventory = inventories.stream()
                .filter(inv -> inv.getAvailableQuantity() >= request.quantity())
                .findFirst()
                .orElseThrow(() -> new BusinessException("Insufficient stock available", HttpStatus.CONFLICT));

        // Adjust quantities
        selectedInventory.setAvailableQuantity(selectedInventory.getAvailableQuantity() - request.quantity());
        selectedInventory.setReservedQuantity(selectedInventory.getReservedQuantity() + request.quantity());
        inventoryRepository.save(selectedInventory);
        
        syncProductStockQuantity(product);

        InventoryReservation reservation = new InventoryReservation();
        reservation.setInventory(selectedInventory);
        reservation.setCartId(request.cartId());
        reservation.setOrderId(request.orderId());
        reservation.setQuantity(request.quantity());
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // 15 mins lock

        reservation = reservationRepository.save(reservation);
        
        log.info("Reserved {} units of product {} at {}", request.quantity(), request.productId(), selectedInventory.getLocation());
        return inventoryMapper.toDto(reservation);
    }
    
    @Transactional
    public void confirmReservation(Long reservationId, Long orderId) {
        InventoryReservation reservation = getReservationEntityById(reservationId);
        
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessException("Reservation is not active", HttpStatus.BAD_REQUEST);
        }
        
        // Convert reservation into actual order allocation
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setOrderId(orderId);
        reservationRepository.save(reservation);
        
        // Deduct from reserved quantity, effectively permanently removing it from inventory
        Inventory inventory = reservation.getInventory();
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
        inventoryRepository.save(inventory);
        
        syncProductStockQuantity(inventory.getProduct());
        log.info("Confirmed reservation {} for order {}", reservationId, orderId);
    }
    
    @Transactional
    public void cancelReservation(Long reservationId) {
        InventoryReservation reservation = getReservationEntityById(reservationId);
        
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessException("Reservation is not active", HttpStatus.BAD_REQUEST);
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        
        // Return quantity to available pool
        Inventory inventory = reservation.getInventory();
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
        inventoryRepository.save(inventory);
        
        syncProductStockQuantity(inventory.getProduct());
        log.info("Cancelled reservation {}", reservationId);
    }

    // --- Private Helpers ---

    private Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
    }
    
    private InventoryReservation getReservationEntityById(Long id) {
        return reservationRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new BusinessException("Reservation not found", HttpStatus.NOT_FOUND));
    }

    private void syncProductStockQuantity(Product product) {
        List<Inventory> inventories = inventoryRepository.findByProductIdAndIsDeletedFalse(product.getId());
        int totalAvailable = inventories.stream().mapToInt(Inventory::getAvailableQuantity).sum();
        product.setStockQuantity(totalAvailable);
        productRepository.save(product);
    }
}
