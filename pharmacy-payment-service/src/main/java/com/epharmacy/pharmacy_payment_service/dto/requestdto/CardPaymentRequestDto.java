package com.epharmacy.pharmacy_payment_service.dto.requestdto;

import java.time.LocalDate;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class CardPaymentRequestDto {
	private String cardId;
	private String nameOnCard;
	private String cartType;
	private String cvv;
	private LocalDate expiryDate;
	
	
	public CardPaymentRequestDto(String cardId, String nameOnCard, String cartType, String cvv, LocalDate expiryDate) {
		
		this.cardId = cardId;
		this.nameOnCard = nameOnCard;
		this.cartType = cartType;
		this.cvv = cvv;
		this.expiryDate = expiryDate;
	}
	
	
	
}
