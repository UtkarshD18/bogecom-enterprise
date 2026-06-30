package com.bogecom.cart.repository;

import com.bogecom.cart.entity.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductIdAndIsDeletedFalse(Long cartId, Long productId);
}
