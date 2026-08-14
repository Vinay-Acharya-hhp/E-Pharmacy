package com.epharmacy.pharmacy_cart_service.dto.responsedto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponseDto {
	private Long cartId;
	
	private Long customerId;
	
	private Long medicineId;
	
	private Integer quantity;
}
