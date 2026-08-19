package com.epharmacy.pharmacy_user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epharmacy.pharmacy_user_service.entity.Address;

public interface AddressRepo extends JpaRepository<Address,Long>{
	
	List<Address> findByCustomerId(Long customerId);
	 Optional<Address> findByIdAndCustomer_Id(Long addressId,Long customerId);

}
