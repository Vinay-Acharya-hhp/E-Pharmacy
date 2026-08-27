package com.epharmacy.pharmacy_order_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epharmacy.pharmacy_order_service.entity.OrderItem;

public interface OrderItemrepo extends JpaRepository<OrderItem,Long>{
 List<OrderItem>findByOrderItemId(Long orderItemId);
 List<OrderItem>findByOrder_OrderId(Long orderId);
}
