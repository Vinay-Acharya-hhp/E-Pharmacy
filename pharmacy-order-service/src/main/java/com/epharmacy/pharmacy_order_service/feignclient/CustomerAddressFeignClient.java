package com.epharmacy.pharmacy_order_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.epharmacy.pharmacy_order_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_order_service.dto.responsedto.AddressDTO;


@FeignClient(name="PHARMACY-USER-SERVICE")
public interface CustomerAddressFeignClient {
	@GetMapping("customer/getaddress/{addressId}")
  ApiResponse< AddressDTO> getaddress(@PathVariable Long addressId,
			@RequestHeader("id")Long customerId);
}
