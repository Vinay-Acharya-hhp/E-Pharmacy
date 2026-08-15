package com.epharmacy.pharmacy_cart_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epharmacy.pharmacy_cart_service.apiresponse.ApiResponse;
import com.epharmacy.pharmacy_cart_service.cartservice.CartService;
import com.epharmacy.pharmacy_cart_service.dto.requestdto.AddCartRequestDto;
import com.epharmacy.pharmacy_cart_service.dto.requestdto.UpdateQuantityDto;
import com.epharmacy.pharmacy_cart_service.dto.responsedto.CartResponseDto;

@RestController
@RequestMapping("/cart")
public class CartContoller {
	
	private final CartService service;
	

	public CartContoller(CartService service) {
		this.service = service;
	}
	
	
	@PostMapping("/addcart/{medicineId}/{customerId}")
	public ResponseEntity<ApiResponse<CartResponseDto>> addMedicineToCart(@PathVariable Long medicineId,
			@PathVariable Long customerId,
            @RequestBody AddCartRequestDto addrequestdto) {
		CartResponseDto data =service.addMedicineToCart(medicineId, customerId, addrequestdto);
		ApiResponse<CartResponseDto> response=new ApiResponse<>(data,true,HttpStatus.CREATED.value());
		
		return  new ResponseEntity<>(response,HttpStatus.CREATED);
	}
	
	@GetMapping("/getcart/{customerId}")
public ResponseEntity<ApiResponse<List<CartResponseDto>>> getCartMedicine(@PathVariable Long customerId){
	List<CartResponseDto> data=service.getCartMedicine(customerId);
	ApiResponse<List<CartResponseDto>> response=new ApiResponse<>(data,true,HttpStatus.OK.value());
	return new ResponseEntity<>(response,HttpStatus.OK);
}

	@PutMapping("/updatecart/{medicineId}/{customerId}")
public ResponseEntity<ApiResponse<CartResponseDto>> updateQuantity(@PathVariable Long medicineId,
		@PathVariable Long customerId,
		@RequestBody UpdateQuantityDto updatequantiyrequestdto) {
	CartResponseDto data =service.updateQuantity(medicineId, customerId, updatequantiyrequestdto);
	ApiResponse<CartResponseDto> response=new ApiResponse<>(data,true,HttpStatus.OK.value());
	return new ResponseEntity<>(response,HttpStatus.OK);
}
	@DeleteMapping("/deletecart/{customerId}")
 public  ResponseEntity<ApiResponse> deleteMedicine(@PathVariable Long medicineId, @PathVariable Long customerId) {
	 service.deleteMedicine(medicineId, customerId);
	ApiResponse response=new ApiResponse<>("Cart Item Deleted",true,HttpStatus.OK.value());
	return new ResponseEntity<>(response,HttpStatus.OK);
}
    @DeleteMapping("/deleteallcart/{customerId}")
	 public ResponseEntity<ApiResponse> deleteAllMedicine( @PathVariable Long customerId) {
		 service.deleteAllMedicine( customerId);
		ApiResponse response=new ApiResponse<>("Cart All Item Deleted",true,HttpStatus.OK.value());
		return new ResponseEntity<>(response,HttpStatus.OK);
}

}
