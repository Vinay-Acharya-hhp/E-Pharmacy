package com.epharmacy.pharmacy_order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epharmacy.pharmacy_order_service.entity.OrderItem;

public interface OrderItemrepo extends JpaRepository<OrderItem,Long>{

}
