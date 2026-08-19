package com.epharmacy.pharmacy_order_service.dto.responsedto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.epharmacy.pharmacy_order_service.entity.DeliveryStatus;
import com.epharmacy.pharmacy_order_service.entity.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor

public class OrderResponseDto {

	private Long orderId;
	private Long customerId;
	private Long addressId;
	private Double ordervalueBeforeDiscount;
	private Double discountAmount;
	private Double healthCoinUsed;
	private Double finalAmount;
	private LocalDateTime orderDate;
	private LocalDate expectedDeliveryDate;
	private OrderStatus orderStatus;
	private DeliveryStatus deliveryStatus;
	private String cancelReson;
	
	
	
	
}
