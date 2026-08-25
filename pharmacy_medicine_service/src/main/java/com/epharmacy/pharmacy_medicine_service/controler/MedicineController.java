package com.epharmacy.pharmacy_medicine_service.controler;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epharmacy.pharmacy_medicine_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_medicine_service.dto.requestdto.MedicineRequestDTO;
import com.epharmacy.pharmacy_medicine_service.dto.responsedto.MedicineResponseDTO;
import com.epharmacy.pharmacy_medicine_service.service.MedicineService;

@RestController
@RequestMapping("/medicine")
public class MedicineController {
	
	private MedicineService service;

	public MedicineController(MedicineService service) {
		
		this.service = service;
	}
	
    @PostMapping("/add-medicine")
	public ResponseEntity<ApiResponse<MedicineResponseDTO>> addMedicine(@RequestBody MedicineRequestDTO medicineReq) {
		MedicineResponseDTO data=service.addMedicine(medicineReq);
		ApiResponse<MedicineResponseDTO> response=new ApiResponse<>(data,true,201);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
		
	}
    
    @GetMapping("/get-all/{pagenumber}")
	public ResponseEntity<ApiResponse<Page <MedicineResponseDTO>>> getAll
	                                                                 (@PathVariable("pagenumber") int pagenumber,
			                                                         @RequestParam(defaultValue="10") int size){
    Page<MedicineResponseDTO> data=service.getAll(pagenumber, size);
		ApiResponse<Page <MedicineResponseDTO>> response=new ApiResponse<>(data,true,200);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
    
    @GetMapping("/get-category/{category}/{pagenumber}")
	public ResponseEntity<ApiResponse<Page <MedicineResponseDTO>>> getAllbycatogary
	                                              (@PathVariable String category,
	                                            	  @PathVariable("pagenumber") int pagenumber,
	                                               @RequestParam(defaultValue="10")int size){
    	Page<MedicineResponseDTO> data=service.getAllbycatogary(category, pagenumber, size);
		ApiResponse<Page <MedicineResponseDTO>> response=new ApiResponse<>(data,true,200);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
    @GetMapping("/getbyid/{medicineId}")
	public ResponseEntity<ApiResponse<MedicineResponseDTO>> getById(@PathVariable Long medicineId) {
	 	MedicineResponseDTO data=service.getById(medicineId);
			ApiResponse <MedicineResponseDTO> response=new ApiResponse<>(data,true,200);
			return new ResponseEntity<>(response,HttpStatus.OK);
	}

    @PutMapping("/update-stock/{medicineId}")
    public ResponseEntity<ApiResponse<String>> updateStock(@PathVariable Long medicineId, 
    		@RequestBody Integer orderedQuantity) {
     	service.updateStock(medicineId, orderedQuantity);
     	ApiResponse <String> response=new ApiResponse<>("stock updated",true,200);
		return new ResponseEntity<>(response,HttpStatus.OK);
    }
	
	
	
}
