package com.epharmacy.pharmacy_user_service.service;

import com.epharmacy.pharmacy_user_service.customerDto.LoginRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.LoginResponseDTO;
import com.epharmacy.pharmacy_user_service.customerDto.RegisterDTO;

public interface CustomerService {
	
	 String registration(RegisterDTO registerdto);
	 
	LoginResponseDTO login(LoginRequestDTO logindto);
	 

}
