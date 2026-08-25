package com.epharmacy.pharmacy_order_service.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
	
	public class OrderItemResponseDto {

	    private Long orderItemId;

	    private Long medicineId;

	    private String medicineName;

	    private Integer quantity;

	    private Double price;
	
}
