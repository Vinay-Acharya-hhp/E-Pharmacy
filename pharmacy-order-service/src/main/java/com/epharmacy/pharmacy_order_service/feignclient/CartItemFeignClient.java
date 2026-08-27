package com.epharmacy.pharmacy_order_service.feignclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.epharmacy.pharmacy_order_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_order_service.dto.responsedto.CartResponseDto;



@FeignClient(name="PHARMACY-CART-SERVICE")
public interface CartItemFeignClient {
	@GetMapping("cart/getcart")
	ApiResponse<List<CartResponseDto>> getCartMedicine();
	@DeleteMapping("cart/deleteallcart")
	 public ResponseEntity<ApiResponse<String>> deleteAllMedicine();
}
 
