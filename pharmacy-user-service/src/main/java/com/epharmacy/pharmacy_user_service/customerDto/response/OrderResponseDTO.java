package com.epharmacy.pharmacy_user_service.customerDto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;



import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponseDTO {
	

		private Long orderId;
		private Long customerId;
		private Long addressId;
		private Double ordervalueBeforeDiscount;
		private Double discountAmount;
		private Double healthCoinUsed;
		private Double finalAmount;
		private LocalDateTime orderDate;
		private LocalDate expectedDeliveryDate;
		private String orderStatus;
		private String deliveryStatus;
		private String cancelReson;
	}


