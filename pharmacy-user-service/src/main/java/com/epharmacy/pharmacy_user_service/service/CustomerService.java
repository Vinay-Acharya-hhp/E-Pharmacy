package com.epharmacy.pharmacy_user_service.service;

import java.util.List;


import com.epharmacy.pharmacy_user_service.customerDto.request.CustomerRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.LoginRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.PasswordReqestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.RegisterDTO;
import com.epharmacy.pharmacy_user_service.customerDto.response.AddressDTO;
import com.epharmacy.pharmacy_user_service.customerDto.response.CustomerResponseDTO;


public interface CustomerService {
	
	 String registration(RegisterDTO registerdto);
	 
	String login(LoginRequestDTO logindto);
	
	CustomerResponseDTO viewProfile(Long customerId);
	
	String updateProfile(Long customerId,CustomerRequestDTO customerRequestdto);
	
	List<AddressDTO> viewAddress(Long customerId);
	
	String addAddress(Long customerId , AddressDTO address);
	 
	String changePassword(String email ,PasswordReqestDTO passwordRequestdto );
	
	

}
