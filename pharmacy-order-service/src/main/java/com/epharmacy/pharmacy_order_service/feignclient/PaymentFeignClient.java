package com.epharmacy.pharmacy_order_service.feignclient;


import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.epharmacy.pharmacy_order_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_order_service.dto.requestdto.PaymentRequestDto;
import com.epharmacy.pharmacy_order_service.dto.responsedto.MedicineResponseDTO;
import com.epharmacy.pharmacy_order_service.dto.responsedto.PaymentResponseDto;


@FeignClient(name="PHARMACY-PAYMENT-SERVICE")
public interface PaymentFeignClient {

@PostMapping("/amount/{amountTopay}")
 ApiResponse<PaymentResponseDto> makePayment(@PathVariable Double amountTopay,
		@RequestBody PaymentRequestDto paymentRequestDto);

}

