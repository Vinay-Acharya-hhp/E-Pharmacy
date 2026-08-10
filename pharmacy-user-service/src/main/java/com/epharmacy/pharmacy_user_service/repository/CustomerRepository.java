package com.epharmacy.pharmacy_user_service.repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epharmacy.pharmacy_user_service.customerDto.response.CustomerResponseDTO;
import com.epharmacy.pharmacy_user_service.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
	                          
	 Optional<Customer> findByCustomerEmailId(String customerEmailId);
      
	 
	 boolean existsByCustomerEmailId(String email);
}
