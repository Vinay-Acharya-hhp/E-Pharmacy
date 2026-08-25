package com.epharmacy.pharmacy_order_service.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
	private String cardId;
	private String cvv;
	
}
