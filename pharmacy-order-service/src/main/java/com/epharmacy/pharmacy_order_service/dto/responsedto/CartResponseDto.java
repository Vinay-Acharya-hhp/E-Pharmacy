package com.epharmacy.pharmacy_order_service.dto.responsedto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
	private Long cartId;
	
	private Long customerId;
	
	private Long medicineId;
	
	private Integer quantity;
}

