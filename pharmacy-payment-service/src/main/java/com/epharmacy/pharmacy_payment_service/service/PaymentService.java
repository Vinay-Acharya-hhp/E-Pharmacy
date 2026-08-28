package com.epharmacy.pharmacy_payment_service.service;

import java.util.List;

import com.epharmacy.pharmacy_payment_service.dto.requestdto.CardPaymentRequestDto;
import com.epharmacy.pharmacy_payment_service.dto.requestdto.PayOrderRequestDto;
import com.epharmacy.pharmacy_payment_service.dto.requestdto.PaymentRequestDto;
import com.epharmacy.pharmacy_payment_service.dto.responsedto.CardResponseDto;
import com.epharmacy.pharmacy_payment_service.dto.responsedto.PaymentResponseDto;


public interface PaymentService {

	CardResponseDto addCard(Long customerId,CardPaymentRequestDto card);
	List<CardResponseDto> viewCards(Long customerId);
	PaymentResponseDto payForOrder(Long customerId,PayOrderRequestDto payOrderRequestDto);
}
 
//
//PaymentResponseDto processPayment(
//          PaymentRequestDto request
//  );
//
//String makePayment(Long customerId,Double amountToPay,PaymentRequestDto paymentRequestDto );
//PaymentResponseDto makePayment(Long customerId, Double amountTopay,PaymentRequestDto paymentRequestDto );