package com.epharmacy.pharmacy_payment_service.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cards")
public class Card {
	@Id
	private String cardId;
	private String nameOnCard;
	@Enumerated(EnumType.STRING)
	private CardType cardType;
	private String cvv;
	private Double balance;
	private LocalDate expiryDate;
	private Long customerId;

}
