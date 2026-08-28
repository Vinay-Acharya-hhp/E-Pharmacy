package com.epharmacy.pharmacy_payment_service.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderRequestDto {
	private Long orderId;
	   private String cardId;
	   private String cvv;
}

