package com.epharmacy.pharmacy_user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epharmacy.pharmacy_user_service.entity.Customer;

public interface CustomerReository extends JpaRepository<Customer,Long> {
	

}
