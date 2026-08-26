package com.epharmacy.pharmacy_user_service.configuration;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_user_service.entity.Customer;
import com.epharmacy.pharmacy_user_service.entity.UsersPrincipal;
import com.epharmacy.pharmacy_user_service.repository.CustomerRepository;

@Service
public class MyUserDetailService  implements UserDetailsService {
	
	
		
		//@Autowired
		private CustomerRepository repo;
		

		public MyUserDetailService(CustomerRepository repo) {
		
			this.repo = repo;
		}


		@Override
		public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Customer customer=repo.findByCustomerEmailId(email.trim().toLowerCase()).orElseThrow(()->  new UsernameNotFoundException("User not found: " + email));
		
			return new UsersPrincipal(customer);
		}

	}

