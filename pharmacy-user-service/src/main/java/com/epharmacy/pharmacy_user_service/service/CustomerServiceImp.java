package com.epharmacy.pharmacy_user_service.service;

import java.util.List;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_user_service.configuration.JWTService;
import com.epharmacy.pharmacy_user_service.customerDto.request.CustomerRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.LoginRequestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.PasswordReqestDTO;
import com.epharmacy.pharmacy_user_service.customerDto.request.RegisterDTO;
import com.epharmacy.pharmacy_user_service.customerDto.response.AddressDTO;
import com.epharmacy.pharmacy_user_service.customerDto.response.CustomerResponseDTO;
import com.epharmacy.pharmacy_user_service.customerDto.response.LoginResponseDTO;
import com.epharmacy.pharmacy_user_service.entity.Address;
import com.epharmacy.pharmacy_user_service.entity.Customer;
import com.epharmacy.pharmacy_user_service.exception.ResourceAlreadyExistsException;
import com.epharmacy.pharmacy_user_service.exception.UserNotFoundException;
import com.epharmacy.pharmacy_user_service.repository.AddressRepo;
import com.epharmacy.pharmacy_user_service.repository.CustomerRepository;


@Service

public class CustomerServiceImp implements CustomerService {
	
	
	private JWTService jwtservice;
	
	
	private AuthenticationManager authmanager;
	
	//private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);
	
	
	private PasswordEncoder passwordencoder;
	private ModelMapper modelmapper;
	private CustomerRepository repo;
	
	private AddressRepo addressrepo;
	
	public CustomerServiceImp(ModelMapper modelmapper,
			CustomerRepository repo,AddressRepo addressrepo,
			PasswordEncoder passwordencoder,AuthenticationManager authmanager,JWTService jwtservice) 
	{
		this.modelmapper=modelmapper;
		this.repo=repo;
		this.addressrepo=addressrepo;
		this.passwordencoder=passwordencoder;
		this.authmanager=authmanager;
		this.jwtservice=jwtservice;
	}
	

	@Override
	public String registration(RegisterDTO registerdto) {
		
		if(repo.existsByCustomerEmailId(registerdto.getCustomerEmailId())){
			throw new ResourceAlreadyExistsException("Email AlreadyExists");
		}
		
				
		Customer customer=modelmapper.map(registerdto, Customer.class);
		customer.setPassword(passwordencoder.encode(registerdto.getPassword()));
		
		 for(Address address: customer.getAddress()) {
			 address.setCustomer(customer);
		 }
		 repo.save(customer);
		 return "Registration Successfull";
	}
	
    @Override
	public String login(LoginRequestDTO logindto) {
    	 System.out.println(logindto.getCustomerEmailId());
    	 System.out.println(logindto.getPassword());
    	 
    	Authentication authenticat= authmanager.authenticate(new UsernamePasswordAuthenticationToken
    			(logindto.getCustomerEmailId(),logindto.getPassword()));
    	
	    if(authenticat.isAuthenticated()) {
	    	 String token = jwtservice.generateToken(logindto.getCustomerEmailId());
	 
     System.out.println(token);
	    
	    	return token;
	    }
     return "Login failed";
	}


	@Override
	public CustomerResponseDTO viewProfile(Long customerId) {
		Customer customer = repo.findById(customerId).orElseThrow(()->new UserNotFoundException("user Not Found"));
		CustomerResponseDTO profile=modelmapper.map(customer,CustomerResponseDTO.class);
		
		return profile;
	}


	@Override
	public String updateProfile(Long customerId, CustomerRequestDTO customerRequestdto) {
		Customer customer = repo.findById(customerId).orElseThrow(()->new UserNotFoundException("user Not Found"));
		customer.setCustomerName(customerRequestdto.getContactNumber());
		customer.setContactNumber(customerRequestdto.getContactNumber());
		customer.setCustomerEmailId(customerRequestdto.getCustomerEmailId());
		repo.save(customer);
		return "profile updated successfully";
	}


	@Override
	public List<AddressDTO> viewAddress(Long customerId) {
		Customer customer = repo.findById(customerId).orElseThrow(()->
		                                   new UserNotFoundException("user Not Found"));
		
		return customer.getAddress()
				.stream()
				.map(address -> modelmapper.map(address, AddressDTO.class))
				.toList();
	}


	@Override
	public String addAddress(Long customerId, AddressDTO addressdto) {
		Customer customer = repo.findById(customerId).orElseThrow(()->new UserNotFoundException("user Not Found"));
		Address address =new Address();
		address.setAddressName(addressdto.getAddressName());
		address.setAddressLine1(addressdto.getAddressLine1());
		address.setAddressLine2(addressdto.getAddressLine2());
		address.setArea(addressdto.getArea());
		address.setCity(addressdto.getCity());
		address.setPincode(addressdto.getPincode());
		address.setState(addressdto.getState());
		address.setCustomer(customer);
		addressrepo.save(address);
		return "Address added successfully" ;
	}


	@Override
	public String changePassword(Long id,PasswordReqestDTO passwordRequestdto) {
		Customer customer = repo.findById(id).orElse(null);
		
		return " ";
	}

}
