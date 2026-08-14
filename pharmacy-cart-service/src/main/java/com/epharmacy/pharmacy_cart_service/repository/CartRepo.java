package com.epharmacy.pharmacy_cart_service.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epharmacy.pharmacy_cart_service.entity.Cart;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomerIdAndMedicineId(
            Long customerId,
            Long medicineId
    );

    List<Cart> findByCustomerId(Long customerId);

    void deleteByCustomerIdAndMedicineId(
            Long customerId,
            Long medicineId
    );

    void deleteByCustomerId(Long customerId);
}
