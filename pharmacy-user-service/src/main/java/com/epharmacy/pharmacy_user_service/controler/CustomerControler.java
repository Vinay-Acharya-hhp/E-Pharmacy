package com.epharmacy.pharmacy_user_service.controler;

import java.util.List;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epharmacy.pharmacy_user_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_user_service.configuration.JWTService;
import com.epharmacy.pharmacy_user_service.customerDto.request.CustomerRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.LoginRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.PasswordReqestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.RegisterDTO;
import com.epharmacy.pharmacy_user_service.customerDto.response.AddressDTO;
import com.epharmacy.pharmacy_user_service.customerDto.response.CustomerResponseDTO;
import com.epharmacy.pharmacy_user_service.customerDto.response.LoginResponseDTO;
import com.epharmacy.pharmacy_user_service.entity.Customer;
import com.epharmacy.pharmacy_user_service.service.CustomerService;
import com.epharmacy.pharmacy_user_service.service.CustomerServiceImp;

@RestController
@RequestMapping("/customer")
public class CustomerControler {
	private JWTService jwtService;
	private CustomerService service;
    
	
	

	public CustomerControler(JWTService jwtService, CustomerService service) {
		
		this.jwtService = jwtService;
		this.service = service;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<String>> registration
	(@RequestBody RegisterDTO registerdto) {
		String data = service.registration(registerdto);
		ApiResponse<String> response=new ApiResponse<>(data,true,201);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}
    
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequestDTO logindto) {
		String token=service.login(logindto);
		HttpHeaders headers=new HttpHeaders();
		headers.setBearerAuth(token);
		
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(token);
	}
	
	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<CustomerResponseDTO>> viewProfile(
	        @RequestHeader("Authorization") String authorization)
	        {

	    String token = authorization.trim();

	    if (token.startsWith("Bearer ")) {
	        token = token.substring(7).trim();
	    }

	    Long customerId = jwtService.extractId(token);

	    System.out.println("This is customer ID: " + customerId);
	   

	    CustomerResponseDTO data = service.viewProfile(customerId);

	    ApiResponse<CustomerResponseDTO> response =
	            new ApiResponse<>(data, true, 200);

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}


	@PutMapping("/update-profile")
	public ResponseEntity<ApiResponse<String>> updateProfile
	( @RequestBody CustomerRequestDTO customerRequestdto,@RequestHeader("Authorization")String authorization ) {
		String token =authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId=jwtService.extractId(token);
		String data=service.updateProfile(customerId, customerRequestdto);
		 ApiResponse<String> response=new ApiResponse<>(data,true,200);
			return new ResponseEntity<>(response,HttpStatus.OK);
	}


	@GetMapping("/view-address")
	public ResponseEntity<ApiResponse<List<AddressDTO>>> viewAddress
	(@RequestHeader("Authorization")String authorization) {
		String token=authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId=jwtService.extractId(token);
		
		List<AddressDTO> data= service.viewAddress(customerId);
		 ApiResponse<List<AddressDTO>> response=new ApiResponse<>(data,true,200);
			return new ResponseEntity<>(response,HttpStatus.OK);
		
	}


	@PostMapping("/add-address")
	public ResponseEntity<ApiResponse<String>> add_Address
	( @RequestBody AddressDTO address,
	  @RequestHeader("Authorization")String authorization) {
		String token=authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId= jwtService.extractId(token);
		 String data=service.addAddress(customerId, address);
		 ApiResponse<String> response=new ApiResponse<>(data,true,201);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}


	@PutMapping("/change-password")
	public ResponseEntity<ApiResponse<String>> changePassword
	(@RequestBody PasswordReqestDTO passwordRequestdto,
	@RequestHeader("Authorization")String authorization) {
		String token=authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId=jwtService.extractId(token);
		String data=service.changePassword(customerId, passwordRequestdto);
		ApiResponse<String> response= new ApiResponse<>(data,true,200);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@GetMapping("/getaddress/{addressId}")
	public ResponseEntity<ApiResponse<AddressDTO>> getaddress(@PathVariable Long addressId,
			@RequestHeader("Authorization")String authorization)
	{
		String token=authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId=jwtService.extractId(token);
		AddressDTO data =service.getaddress(addressId, customerId);
		ApiResponse<AddressDTO> response= new ApiResponse<>(data,true,200);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

}
