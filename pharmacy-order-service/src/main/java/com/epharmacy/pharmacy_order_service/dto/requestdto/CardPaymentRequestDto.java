package com.epharmacy.pharmacy_order_service.dto.requestdto;

import java.time.LocalDate;

import com.epharmacy.pharmacy_order_service.entity.CardType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentRequestDto {
private String cardId;
private String nameOncard;
private CardType cardType;
private String cvv;
private LocalDate expirydate;

}
