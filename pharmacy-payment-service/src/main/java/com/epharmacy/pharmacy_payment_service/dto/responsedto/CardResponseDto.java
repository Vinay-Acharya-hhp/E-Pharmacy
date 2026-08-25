package com.epharmacy.pharmacy_payment_service.dto.responsedto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDto {
	private String cardId;
	private String nameOnCard;
	private String cartType;
	private String cvv;
	private LocalDate expiryDate;
	private Long customerId;
}
