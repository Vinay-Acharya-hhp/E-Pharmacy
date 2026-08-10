package com.epharmacy.pharmacy_user_service.entity;

import java.util.Collection;
import java.util.Collections;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



public class UsersPrincipal implements UserDetails{

		private Customer customer;
		
		
		
		public UsersPrincipal(Customer customer) {
			this.customer = customer;
		}


		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			// TODO Auto-generated method stub
			return Collections.emptyList(); 
	/*Collections.singleton(new SimpleGrantedAuthority("ROLE_"+user.getType().name()));*/
		}
		

		@Override
		public @Nullable String getPassword() {
			// TODO Auto-generated method stub
			
			return customer.getPassword();
		}

		@Override
		public String getUsername() {
			// TODO Auto-generated method stub
			return customer.getCustomerEmailId();
		}

		@Override
		public boolean isAccountNonExpired() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return true;
		}


	}

