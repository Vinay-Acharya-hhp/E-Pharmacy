package com.epharmacy.pharmacy_payment_service.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
private boolean success;
private String message;
private Long customerId;
private String transactionId;

}
