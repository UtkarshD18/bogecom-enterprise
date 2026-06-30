package com.bogecom.cart.repository;

import com.bogecom.cart.entity.Cart;
import com.bogecom.cart.entity.CartStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> findByUserIdAndStatusAndIsDeletedFalse(Long userId, CartStatus status);

  Optional<Cart> findBySessionIdAndStatusAndIsDeletedFalse(String sessionId, CartStatus status);
}
