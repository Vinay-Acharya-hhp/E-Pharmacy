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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epharmacy.pharmacy_cart_service.apiresponse.ApiResponse;
import com.epharmacy.pharmacy_cart_service.cartservice.CartService;
import com.epharmacy.pharmacy_cart_service.configuration.JWTService;
import com.epharmacy.pharmacy_cart_service.dto.requestdto.AddCartRequestDto;
import com.epharmacy.pharmacy_cart_service.dto.requestdto.UpdateQuantityDto;
import com.epharmacy.pharmacy_cart_service.dto.responsedto.CartResponseDto;

@RestController
@RequestMapping("/cart")
public class CartContoller {
	
	private final CartService service;
	private final JWTService jwtService;
	
	public CartContoller(CartService service, JWTService jwtService) {
	
		this.service = service;
		this.jwtService = jwtService;
	}

	@PostMapping("/addcart/{medicineId}")
	public ResponseEntity<ApiResponse<CartResponseDto>> addMedicineToCart(@PathVariable Long medicineId,
			@RequestHeader("Authorization")String authorization,
            @RequestBody AddCartRequestDto addrequestdto) {
		String token =authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId=jwtService.extractCustomerId(token);
		
		
		CartResponseDto data =service.addMedicineToCart(medicineId, customerId, addrequestdto);
		ApiResponse<CartResponseDto> response=new ApiResponse<>(data,true,HttpStatus.CREATED.value());
		
		return  new ResponseEntity<>(response,HttpStatus.CREATED);
	}
	
	
	@GetMapping("/getcart")
public ResponseEntity<ApiResponse<List<CartResponseDto>>> getCartMedicine
                                         (@RequestHeader("Authorization")String authorization){
		String token =authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId=jwtService.extractCustomerId(token);
	List<CartResponseDto> data=service.getCartMedicine(customerId);
	ApiResponse<List<CartResponseDto>> response=new ApiResponse<>
	                                         (data,true,HttpStatus.OK.value());
	return new ResponseEntity<>(response,HttpStatus.OK);
}

	
	@PutMapping("/updatecart/{medicineId}")
public ResponseEntity<ApiResponse<CartResponseDto>> updateQuantity
                              (@PathVariable Long medicineId,
                            		  @RequestHeader("Authorization")String authorization,
		                      @RequestBody UpdateQuantityDto updatequantiyrequestdto) {
		String token =authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId=jwtService.extractCustomerId(token);
	CartResponseDto data =service.updateQuantity
			 (medicineId, customerId, updatequantiyrequestdto);
	ApiResponse<CartResponseDto> response=new ApiResponse<>
	                               (data,true,HttpStatus.OK.value());
	return new ResponseEntity<>(response,HttpStatus.OK);
}
	
	
	@DeleteMapping("/deletecart/{medicineId}")
 public  ResponseEntity<ApiResponse> deleteMedicine
                     (@PathVariable Long medicineId,
                    	@RequestHeader("Authorization")String authorization ) {  
String token =authorization.trim();
if(token.startsWith("Bearer ")) {
token=token.substring(7).trim();
}
Long customerId=jwtService.extractCustomerId(token);
	 service.deleteMedicine(medicineId, customerId);
	ApiResponse response=new ApiResponse<>("Cart Item Deleted",true,HttpStatus.OK.value());
	return new ResponseEntity<>(response,HttpStatus.OK);
}
	
	
    @DeleteMapping("/deleteallcart")
	 public ResponseEntity<ApiResponse> deleteAllMedicine
	  ( @RequestHeader("Authorization")String authorization) {
    	String token =authorization.trim();
    	if(token.startsWith("Bearer ")) {
    	token=token.substring(7).trim();
    	}
    	Long customerId=jwtService.extractCustomerId(token);
		 service.deleteAllMedicine( customerId);
		ApiResponse response=new ApiResponse<>("Cart All Item Deleted",true,HttpStatus.OK.value());
		return new ResponseEntity<>(response,HttpStatus.OK);
}

}
