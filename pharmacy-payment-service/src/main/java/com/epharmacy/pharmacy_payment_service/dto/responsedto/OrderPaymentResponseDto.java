package com.epharmacy.pharmacy_payment_service.dto.responsedto;

import com.epharmacy.pharmacy_payment_service.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentResponseDto {
private Long orderId;
private Long customerId;
private double amount;
private OrderStatus orderStatus;
}
