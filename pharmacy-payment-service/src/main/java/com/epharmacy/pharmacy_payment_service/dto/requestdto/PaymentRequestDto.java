package com.epharmacy.pharmacy_payment_service.dto.requestdto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.epharmacy.pharmacy_payment_service.entity.CardType;
import com.epharmacy.pharmacy_payment_service.entity.PaymentStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
        private String cardId;
	    private String nameOnCard;
	    private CardType cardType;
	    private String cvv;
	    private LocalDate expiryMonth;
	    private Long customerId;

	  

}
