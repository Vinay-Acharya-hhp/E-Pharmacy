package com.epharmacy.pharmacy_user_service.controler;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epharmacy.pharmacy_user_service.customerDto.LoginRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.LoginResponseDTO;
import com.epharmacy.pharmacy_user_service.customerDto.RegisterDTO;
import com.epharmacy.pharmacy_user_service.entity.Customer;
import com.epharmacy.pharmacy_user_service.service.CustomerServiceImp;

@RestController
@RequestMapping("/customer")
public class CustomerControler {
	private CustomerServiceImp service;
    
	public CustomerControler (CustomerServiceImp service) {
		this.service=service;
	}
	

	@PostMapping("/register")
	public String registration(@RequestBody RegisterDTO registerdto) {
		return service.registration(registerdto);
	}
    
	@PostMapping("/login")
	public LoginResponseDTO login(@RequestBody LoginRequestDTO logindto) {
    	return service.login(logindto);
	}

}
