package com.epharmacy.pharmacy_payment_service.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epharmacy.pharmacy_payment_service.apiresponse.ApiResponse;
import com.epharmacy.pharmacy_payment_service.configuration.JWTService;
import com.epharmacy.pharmacy_payment_service.dto.requestdto.CardPaymentRequestDto;
import com.epharmacy.pharmacy_payment_service.dto.requestdto.PaymentRequestDto;
import com.epharmacy.pharmacy_payment_service.dto.responsedto.CardResponseDto;
import com.epharmacy.pharmacy_payment_service.dto.responsedto.PaymentResponseDto;
import com.epharmacy.pharmacy_payment_service.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {
	private PaymentService service;
	private JWTService jwtService;
	

	public PaymentController(PaymentService service,JWTService jwtService) {
		this.service = service;
		this.jwtService=jwtService;
	}
	
	
@PostMapping("/amount/{amountTopay}")
public	ResponseEntity<ApiResponse<PaymentResponseDto>> makePayment(
		@RequestHeader("Authorization") String authorization,
		@PathVariable Double amountTopay,
		@RequestBody PaymentRequestDto paymentRequestDto) {
	String token = authorization.trim();
	if (token.startsWith("Bearer ")) {
		token = token.substring(7).trim();
	}
	Long customerId = jwtService.extractCustomerId(token);
System.out.println(customerId);
		PaymentResponseDto data=service.makePayment(customerId, amountTopay, paymentRequestDto);
		
		ApiResponse<PaymentResponseDto> response=new ApiResponse<>(data,true,201);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
		
}
	
	
	
	
	
	
//	@PostMapping("/pay")
//	public ResponseEntity<ApiResponse<PaymentResponseDto>> payment(@RequestBody PaymentRequestDto request) {
//		PaymentResponseDto data=service.processPayment(request);
//		ApiResponse<PaymentResponseDto> response=new ApiResponse<>(data,true,201);
//		return new ResponseEntity<>(response,HttpStatus.CREATED);
//	}
//	@PostMapping("/amount/{amountToPay}")
//	public ResponseEntity<ApiResponse<String>> makePayment(@RequestHeader("id") Long customerId,
//			@PathVariable Double amountToPay,
//			@RequestBody PaymentRequestDto paymentRequestDto ) {
//		String token =authorization.trim();
//		if(token.startsWith("Bearer ")) {
//	    	token=token.substring(7).trim();
//	    	}
//	    	Long customerId=jwtService.extractCustomerId(token);
//		String data=service.makePayment(customerId, amountToPay, paymentRequestDto);
//		ApiResponse<String> response=new ApiResponse<>(data,true,201);
//		return new ResponseEntity<>(response,HttpStatus.CREATED);
//	}

	@PostMapping("/card/addcard")
	public ResponseEntity<ApiResponse<CardResponseDto>> addCard
	(@RequestHeader("Authorization") String authorization,@RequestBody CardPaymentRequestDto cardRequestDto) {
		String token =authorization.trim();
		if(token.startsWith("Bearer ")) {
	    	token=token.substring(7).trim();
	    	}
	    	Long customerId=jwtService.extractCustomerId(token);
	    	System.out.println(customerId);
		CardResponseDto data=service.addCard(customerId, cardRequestDto);
		ApiResponse<CardResponseDto> response=new ApiResponse<>(data,true,201);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
		
	}
	@GetMapping("/getcards")
	public ResponseEntity<ApiResponse<List<CardResponseDto>>> viewCards(@RequestHeader("Authorization") String authorization){
	 	String token =authorization.trim();
    	if(token.startsWith("Bearer ")) {
    	token=token.substring(7).trim();
    	}
    	Long customerId=jwtService.extractCustomerId(token);
		List<CardResponseDto> data=service.viewCards(customerId);
		ApiResponse<List<CardResponseDto>> response=new ApiResponse<>(data,true,200);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
}
