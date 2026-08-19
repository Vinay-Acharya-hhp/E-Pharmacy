package com.epharmacy.pharmacy_cart_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.epharmacy.pharmacy_cart_service.apiresponse.ApiResponse;
import com.epharmacy.pharmacy_cart_service.dto.responsedto.MedicineResponseDTO;


@FeignClient(name="PHARMACY-MEDICINE-SERVICE")
public interface MedicineFeignClient {
	
	@GetMapping("/medicine/getbyid/{medicineId}")
 ApiResponse<MedicineResponseDTO>
	   getById(@PathVariable Long medicineId) ;
	 	
}
