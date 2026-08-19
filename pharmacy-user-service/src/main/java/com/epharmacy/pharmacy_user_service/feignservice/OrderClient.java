package com.epharmacy.pharmacy_user_service.feignservice;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import com.epharmacy.pharmacy_user_service.customerDto.response.CustomerResponseDTO;

@FeignClient(name="PHARMACY-ORDER-SERVICE")
public interface OrderClient {

	@GetMapping("/order/view-order/customer/{customerId}")
	 ResponseEntity<com.epharmacy.pharmacy_user_service.apiResponse.ApiResponse<List<CustomerResponseDTO>>>
	               getCustomerorders(@PathVariable("customerId") Long customerId);
}