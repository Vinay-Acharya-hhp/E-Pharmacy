package com.epharmacy.pharmacy_payment_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epharmacy.pharmacy_payment_service.entity.Payment;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

}
