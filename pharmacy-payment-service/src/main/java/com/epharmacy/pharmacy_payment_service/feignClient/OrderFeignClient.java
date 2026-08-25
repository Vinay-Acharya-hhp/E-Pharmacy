package com.epharmacy.pharmacy_payment_service.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.epharmacy.pharmacy_payment_service.apiresponse.ApiResponse;
import com.epharmacy.pharmacy_payment_service.dto.responsedto.OrderPaymentResponseDto;


@FeignClient(name="PHARMACY-PAYEMENT-SERVICE")
public interface OrderFeignClient {

	@PutMapping("/{orderId}/payment-success")
	ApiResponse<String> paymentSuccess(
	        @PathVariable Long orderId,
	        @RequestParam Long paymentId);

@GetMapping("/getorderid/{orderId}")
ApiResponse<OrderPaymentResponseDto> getidamount(@PathVariable Long orderId);
}