package com.bogecom.order.repository;

import com.bogecom.order.entity.Order;
import com.bogecom.order.entity.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findByUserIdAndIsDeletedFalse(Long userId);

  List<Order> findByStatusAndIsDeletedFalse(OrderStatus status);
}
