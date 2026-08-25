package com.epharmacy.pharmacy_order_service.dto.responsedto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.epharmacy.pharmacy_order_service.entity.DeliveryStatus;
import com.epharmacy.pharmacy_order_service.entity.OrderItem;
import com.epharmacy.pharmacy_order_service.entity.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor

public class OrderResponseDto {

	    private Long orderId;
	    private Long customerId;
	    private Long addressId;
	    
        private Double orderValueBeforeDiscount;
	    private Double discount;
	    private Double amountPaid;

	    private String transactionId;
	    private LocalDateTime orderDate;

	    private LocalDate expectedDeliveryDate;

	    private OrderStatus orderStatus;

	    private DeliveryStatus deliveryStatus;

	    private List<OrderItemResponseDto> orderItems;
	
	
	
	
}
