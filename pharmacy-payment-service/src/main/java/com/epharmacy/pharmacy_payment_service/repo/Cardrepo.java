package com.epharmacy.pharmacy_payment_service.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epharmacy.pharmacy_payment_service.entity.Card;

public interface Cardrepo extends JpaRepository<Card,String>{
	List<Card> findByCustomerId(Long customerId);
	boolean existsByCardIdAndCustomerId(String cardId,Long customerId);
	Optional<Card> findByCardIdAndCustomerId(String cardId,Long customerId);
}
