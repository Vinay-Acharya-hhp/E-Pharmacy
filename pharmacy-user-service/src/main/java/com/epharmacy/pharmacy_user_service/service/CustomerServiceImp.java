package com.epharmacy.pharmacy_user_service.service;

import java.util.List;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
import com.epharmacy.pharmacy_user_service.exception.PasswordException;
import com.epharmacy.pharmacy_user_service.exception.ResourceAlreadyExistsException;
import com.epharmacy.pharmacy_user_service.exception.UserNotFoundException;
import com.epharmacy.pharmacy_user_service.repository.AddressRepo;
import com.epharmacy.pharmacy_user_service.repository.CustomerRepository;


@Service

public class CustomerServiceImp implements CustomerService {
	
	
	private JWTService jwtService;
	
	
	private AuthenticationManager authManager;
	
	//private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);
	
	
	private PasswordEncoder passwordencoder;
	private ModelMapper modelmapper;
	private CustomerRepository repo;
	
	private AddressRepo addressrepo;
	
	public CustomerServiceImp(ModelMapper modelmapper,
			CustomerRepository repo,AddressRepo addressrepo,
			PasswordEncoder passwordencoder,AuthenticationManager authManager,JWTService jwtservice) 
	{
		this.modelmapper=modelmapper;
		this.repo=repo;
		this.addressrepo=addressrepo;
		this.passwordencoder=passwordencoder;
		this.authManager=authManager;
		this.jwtService=jwtservice;
	}
	

	@Override
	public String registration(RegisterDTO registerdto) {
		
		String normalize=registerdto.getCustomerEmailId().trim().toLowerCase();
		if(repo.existsByCustomerEmailId(normalize)){
			throw new ResourceAlreadyExistsException("Email AlreadyExists");
		}
		
				
		Customer customer=modelmapper.map(registerdto, Customer.class);
		customer.setCustomerEmailId(normalize);
		customer.setPassword(passwordencoder.encode(registerdto.getPassword()));
		
		 for(Address address: customer.getAddress()) {
			 address.setCustomer(customer);
		 }
		 repo.save(customer);
		 return "Registration Successfull";
	}
	
    @Override
    public String login(LoginRequestDTO loginDto) {

        String normalizedEmail = loginDto.getCustomerEmailId()
                .trim()
                .toLowerCase();

        try {
            Authentication authentication =
                    authManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    normalizedEmail,
                                    loginDto.getPassword()
                            )
                    );

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(normalizedEmail);
            }

            throw new BadCredentialsException("Invalid email or password");

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }
//	public String login(LoginRequestDTO logindto) {
//    	
//    	String normalizedEmail = logindto.getCustomerEmailId().trim().toLowerCase();
//    	 try {
//    	Authentication authenticat= authmanager.authenticate(new UsernamePasswordAuthenticationToken
//    			(normalizedEmail,logindto.getPassword()));
//    	
//	    if(authenticat.isAuthenticated()) {
//	    	 String token = jwtservice.generateToken(normalizedEmail);
//	    	 return token;
//	   
//	    }
//    	 }catch(Exception e) {
//    		 return e.getMessage();
//    	 }
//    	 	
//	}


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
		customer.setGender(customerRequestdto.getGender());
		//customer.setCustomerEmailId(customerRequestdto.getCustomerEmailId());
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
	public String changePassword(String email,PasswordReqestDTO passwordRequestdto) {
		Customer customer = repo.findByCustomerEmailId(email)
				.orElseThrow(()-> new UserNotFoundException("User Not found"));
		if(!passwordencoder.matches(passwordRequestdto.getOldPassword(), customer.getPassword())) {
			throw new PasswordException("Old password is Incorrect");
		}
		if(!passwordRequestdto.getNewPassword().equals(passwordRequestdto.getConfiremPassword())) {
			throw new PasswordException("New password and Confirm password do not matches");
		}
		if(passwordencoder.matches(passwordRequestdto.getNewPassword(), customer.getPassword())) {
			throw new PasswordException("New password must be different from old password");
			
		}
		String encode=passwordencoder.encode(passwordRequestdto.getNewPassword());
		customer.setPassword(encode);
		return "password changed succesfully ";
	}

}
