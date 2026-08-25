package com.epharmacy.pharmacy_order_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.epharmacy.pharmacy_order_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_order_service.dto.responsedto.MedicineResponseDTO;



@FeignClient(name="PHARMACY-MEDICINE-SERVICE")
public interface MedicineFeignClient {

	@GetMapping("medicine/getbyid/{medicineId}")
	ApiResponse<MedicineResponseDTO> getById(@PathVariable Long medicineId);
	@PutMapping("medicine/update-stock/{medicineId}")
   ApiResponse<String> updateStock(@PathVariable Long medicineId, 
    		@RequestBody Integer orderedQuantity) ;
}
