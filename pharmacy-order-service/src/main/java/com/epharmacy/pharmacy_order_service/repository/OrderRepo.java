package com.epharmacy.pharmacy_order_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epharmacy.pharmacy_order_service.entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long> {
	
	List<Order> findByCustomerId(Long customerId);

}
