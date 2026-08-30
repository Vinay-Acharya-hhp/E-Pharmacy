package com.epharmacy.pharmacy_payment_service.service;

import java.time.LocalDate;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_payment_service.apiresponse.ApiResponse;
import com.epharmacy.pharmacy_payment_service.dto.requestdto.CardPaymentRequestDto;
import com.epharmacy.pharmacy_payment_service.dto.requestdto.PayOrderRequestDto;
import com.epharmacy.pharmacy_payment_service.dto.requestdto.PaymentRequestDto;
import com.epharmacy.pharmacy_payment_service.dto.responsedto.CardResponseDto;
import com.epharmacy.pharmacy_payment_service.dto.responsedto.OrderPaymentResponseDto;
import com.epharmacy.pharmacy_payment_service.dto.responsedto.PaymentResponseDto;
import com.epharmacy.pharmacy_payment_service.entity.Card;
import com.epharmacy.pharmacy_payment_service.entity.CardType;
import com.epharmacy.pharmacy_payment_service.entity.OrderStatus;
import com.epharmacy.pharmacy_payment_service.entity.Payment;
import com.epharmacy.pharmacy_payment_service.entity.PaymentStatus;
import com.epharmacy.pharmacy_payment_service.exception.CardNotFoundException;
import com.epharmacy.pharmacy_payment_service.exception.InvalidAmountException;
import com.epharmacy.pharmacy_payment_service.exception.OrderNotFoundException;
import com.epharmacy.pharmacy_payment_service.feignClient.OrderFeignClient;
import com.epharmacy.pharmacy_payment_service.repo.Cardrepo;
import com.epharmacy.pharmacy_payment_service.repo.PaymentRepo;


import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImp implements PaymentService {
	
	private final PaymentRepo paymentRepo;
	private final ModelMapper modelmapper;
	private final OrderFeignClient orderFeignClient;
	private final Cardrepo cardrepo;

	public PaymentServiceImp(PaymentRepo paymentRepo, ModelMapper modelmapper
			,OrderFeignClient orderFeignClient,Cardrepo cardrepo) {
		this.paymentRepo = paymentRepo;
		this.modelmapper=modelmapper;
		this.orderFeignClient=orderFeignClient;
		this.cardrepo=cardrepo;
	}
	
	


	@Override
	@Transactional
	public PaymentResponseDto payForOrder(Long customerId, PayOrderRequestDto payOrderRequestDto) {

		if (payOrderRequestDto == null || payOrderRequestDto.getOrderId() == null) {
			throw new OrderNotFoundException("orderId is required");
		}

		// 1. Look up the order and derive the amount to charge from it.
		//    The client never supplies the amount for this endpoint.
		OrderPaymentResponseDto orderInfo =
				orderFeignClient
						.getidamount(payOrderRequestDto.getOrderId())
						.getData();
          
		if (orderInfo == null) {
			throw new OrderNotFoundException("Order not found");
		}

		if (!customerId.equals(orderInfo.getCustomerId())) {
			throw new OrderNotFoundException("Order does not belong to this customer");
		}

		if (orderInfo.getOrderStatus() != OrderStatus.PROCESSING) {
			throw new IllegalArgumentException("Order is not waiting for payment");
		}

		Double amountTopay = orderInfo.getAmount();
		if (amountTopay == null || amountTopay <= 0) {
			throw new InvalidAmountException("Order has an invalid amount");
		}

		// 2. Find the card belonging to this customer
		Card card = cardrepo
				.findByCardIdAndCustomerId(
						payOrderRequestDto.getCardId(),
						customerId)
				.orElseThrow(() ->
						new CardNotFoundException("Invalid cardId or customerId"));

		// 3. Validate CVV
		if (!card.getCvv().equals(payOrderRequestDto.getCvv())) {
			throw new CardNotFoundException("Invalid CVV");
		}

		// 4. Validate expiry date
		if (card.getExpiryDate().isBefore(LocalDate.now())) {
			throw new CardNotFoundException("Card has expired");
		}
//
		// 5. Validate balance is sufficient
		if (card.getBalance() == null || card.getBalance() < amountTopay) {
			throw new InvalidAmountException("Insufficient card balance");
		}

		// 6. Debit the card and persist the new balance
		card.setBalance(card.getBalance() - amountTopay);
		cardrepo.save(card);

		// 7. Save the payment record
		String transactionId = "TXN-" + UUID.randomUUID();
        
		Payment payment = new Payment();
		payment.setPaymentId(transactionId);
		payment.setCardNumber(card.getCardId());
		payment.setOrderId(payOrderRequestDto.getOrderId());
		payment.setCustomerId(customerId);
		payment.setAmount(amountTopay);
		payment.setStatus(PaymentStatus.SUCCESS);
		payment.setCreatedAt(LocalDateTime.now());

	    Payment pay=paymentRepo.save(payment);

		// 8. Tell order-service so it can confirm the order,
		//    reduce stock, and clear the customer's cart
//		orderFeignClient.paymentSuccess(
//				payOrderRequestDto.getOrderId(),	
//		    pay.getPaymentId()
//		);

		return new PaymentResponseDto(
				true,
				"Payment made successfully",
				card.getCustomerId(),
				pay.getPaymentId());
	}
	
	
	

	
	@Override
	public CardResponseDto addCard(Long customerId, CardPaymentRequestDto cardPaymentRequestDto) {
		if(customerId==null) {
			throw new CardNotFoundException("Customer Not found");
		}
		if(cardrepo.existsById(cardPaymentRequestDto.getCardId())) {
			throw new CardNotFoundException("Card Already Exists");
		}
		
		//card.setCardType(cardPaymentRequestDto.getCardType());
		Card card= new Card();
        card.setCardId(cardPaymentRequestDto.getCardId());
        card.setCvv(cardPaymentRequestDto.getCvv());
        card.setCardType(cardPaymentRequestDto.getCardType());
	   card.setNameOnCard(cardPaymentRequestDto.getNameOnCard());
	   card.setExpiryDate(cardPaymentRequestDto.getExpiryDate());
	    card.setBalance(cardPaymentRequestDto.getBalance());
		card.setCustomerId(customerId);
		Card save=cardrepo.save(card);
		return modelmapper.map(save, CardResponseDto.class);
	}

	@Override
	public List<CardResponseDto> viewCards(Long customerId) {
		if(customerId==null) {
			throw new CardNotFoundException("Customer Not found");
		}
		List<Card> cards=cardrepo.findByCustomerId(customerId);
		if(cards.isEmpty()) {
			throw new CardNotFoundException("Card Not found");
		}
		
		return cards.stream().map(card ->modelmapper
				.map(card,CardResponseDto.class ))
				.toList();
	}


	}






//@Override
//@Transactional
//public PaymentResponseDto makePayment(Long customerId, Double amountTopay, PaymentRequestDto paymentRequestDto) {
//	 // 1. Find card belonging to customer
//    Card card = cardrepo
//            .findByCardIdAndCustomerId(
//            		paymentRequestDto.getCardId(),
//            		customerId)
//            .orElseThrow(() ->
//                    new IllegalArgumentException(
//                            "Invalid cardId or customerId"));
//
//    // 2. Validate CVV
//    if (!card.getCvv().equals(paymentRequestDto.getCvv())) {
//        throw new IllegalArgumentException("Invalid CVV");
//    }
//
//    // 3. Validate expiry date
//    if (card.getExpiryDate().isBefore(LocalDate.now())) {
//        throw new IllegalArgumentException(
//                "Card has expired");
//    }
//    if(card.getBalance()<=0 || card.getBalance()<amountTopay) {
//    	throw new IllegalArgumentException("Invalid Blance amount");
//    }
//
//    // 4. Validate amount
//    if (amountTopay == null || amountTopay <= 0) {
//        throw new IllegalArgumentException(
//                "Invalid payment amount");
//    }
//
//    // 4b. Validate amount against the real order total
//    //     (never trust the client-supplied amount alone)
//    if (paymentRequestDto.getOrderId() != null) {
//        OrderPaymentResponseDto orderInfo =
//                orderFeignClient
//                        .getidamount(paymentRequestDto.getOrderId())
//                        .getData();
//
//        if (orderInfo == null) {
//            throw new IllegalArgumentException(
//                    "Order not found");
//        }
//
//        if (!customerId.equals(orderInfo.getCustomerId())) {
//            throw new IllegalArgumentException(
//                    "Order does not belong to this customer");
//        }
//
//        if (orderInfo.getOrderStatus() != OrderStatus.PROCESSING) {
//            throw new IllegalArgumentException(
//                    "Order is not waiting for payment");
//        }
//
//        if (Math.abs(orderInfo.getAmount() - amountTopay) > 0.01) {
//            throw new IllegalArgumentException(
//                    "Payment amount does not match order total");
//        }
//    }
//
//    // 5. Save the payment record
//    Payment payment = new Payment();
//    payment.setCardNumber(card.getCardId());
//    payment.setOrderId(paymentRequestDto.getOrderId());
//    payment.setCustomerId(customerId);
//    payment.setAmount(amountTopay);
//    payment.setStatus(PaymentStatus.SUCCESS);
//    payment.setCreatedAt(LocalDateTime.now());
//
//    Payment savedPayment = paymentRepo.save(payment);
//
//    // 6. Tell order-service so it can confirm the order,
//    //    reduce stock, and clear the customer's cart
//    if (paymentRequestDto.getOrderId() != null) {
//        orderFeignClient.paymentSuccess(
//                paymentRequestDto.getOrderId(),
//                savedPayment.getPaymentId()
//        );
//    }
//    
//
//    String transactionId =
//            "TXN-" + UUID.randomUUID();
//
//    return new PaymentResponseDto(
//            true,
//            "Payment made successfully",
//            savedPayment.getCustomerId(),
//            transactionId);
//}
//
//@Override
//@Transactional
//	public PaymentResponseDto processPayment(PaymentRequestDto request) {
//
//	   
//
//	OrderPaymentResponseDto response= orderFeignClient.getidamount(request.getOrderId()).getData();
//	
//	        boolean paymentSuccessful =
//	                simulatePayment(request);
//
//	        if (!paymentSuccessful) {
//
//	            return new PaymentResponseDto(
//	                    null,
//	                    response.getOrderId(),
//	                    response.getAmount(),
//	                    "FAILED",
//	                    "Payment failed"
//	            );
//	        }
//
//	        // -----------------------------
//	        // 6. Save payment
//	        // -----------------------------
//
//	        Payment payment =
//	                new Payment();
//
//	        payment.setOrderId(
//	                response.getOrderId()
//	        );
//
//	        payment.setCustomerId(
//	                request.getCustomerId()
//	        );
//
//	        payment.setAmount(
//	                response.getAmount()
//	        );
//
//	        String cardNumber =
//	                request.getCardNumber();
//
//          payment.setCardNumber(cardNumber);
//	        payment.setExpiryMonth(
//	                request.getExpiryMonth()
//	        );
//
//	        payment.setExpiryYear(
//	                request.getExpiryYear()
//	        );
//
//	        payment.setStatus(
//	                PaymentStatus.SUCCESS
//	        );
//
//	        payment.setCreatedAt(
//	                LocalDateTime.now()
//	        );
//
//	        Payment savedPayment =
//	                repo.save(payment);
//
//	        // -----------------------------
//	        // 7. Tell Order Service
//	        // -----------------------------
//
//	        orderFeignClient.paymentSuccess(
//	                savedPayment.getOrderId(),
//	                savedPayment.getPaymentId()
//	        );
//
//	        // -----------------------------
//	        // 8. Response
//	        // -----------------------------
//
//	        return new PaymentResponseDto(
//	                savedPayment.getPaymentId(),
//	                savedPayment.getOrderId(),
//	                savedPayment.getAmount(),
//	                "SUCCESS",
//	                "Payment successful"
//	        );
//	    }
//
//
//	    private boolean simulatePayment(
//	            PaymentRequestDto request) {
//
//	        /*
//	         * Demo payment.
//	         *
//	         * In a real system this would be
//	         * handled by a payment gateway.
//	         */
//
//	        return true;
//	    }
//	    
//
//


	
//
//	@Override
//	public String makePayment(Long customerId,Double amountToPay, PaymentRequestDto paymentRequestDto) {
//		if(amountToPay==null || amountToPay<=0) {
//			throw new IllegalArgumentException("Amount must be greater than zero");
//		}
//		if(paymentRequestDto==null) {
//			throw new CardNotFoundException("Payment details cannot be null");
//		}
//		Card card=cardrepo.findById(paymentRequestDto.getCardNumber())
//				.orElseThrow(()-> new CardNotFoundException("Payment details cannot be null") );
//		if(card.getCustomerId()!=customerId) {
//			throw new CardNotFoundException("Card does not belong to this customer");
//		}
//		if(card.getExpiryDate().isBefore(LocalDate.now())) {
//			throw new CardNotFoundException("Card has expired");
//			
//		}
//		return "Payment of Rs." +amountToPay +" successful";
//	}

