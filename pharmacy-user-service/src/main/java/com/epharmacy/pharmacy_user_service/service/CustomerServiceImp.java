package com.epharmacy.pharmacy_user_service.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_user_service.customerDto.LoginRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.LoginResponseDTO;
import com.epharmacy.pharmacy_user_service.customerDto.RegisterDTO;
import com.epharmacy.pharmacy_user_service.entity.Address;
import com.epharmacy.pharmacy_user_service.entity.Customer;
import com.epharmacy.pharmacy_user_service.repository.CustomerReository;

@Service

public class CustomerServiceImp implements CustomerService {
	
	
	private ModelMapper modelmapper;
	private CustomerReository repo;
	
	public CustomerServiceImp(ModelMapper modelmapper,CustomerReository repo) {
		this.modelmapper=modelmapper;
		this.repo=repo;
	}
	

	@Override
	public String registration(RegisterDTO registerdto) {
		Customer customer=modelmapper.map(registerdto, Customer.class);
		
		 for(Address address: customer.getAddress()) {
			 address.setCustomer(customer);
		 }
		System.out.println(customer.getDateOfBirth());
		 repo.save(customer);
		 return "Registration Successfull";
	}
    @Override
	public LoginResponseDTO login(LoginRequestDTO logindto) {
    	LoginResponseDTO responsedto = modelmapper.map(logindto, LoginResponseDTO.class);
    	
		return responsedto;
	}

}
