package com.epharmacy.pharmacy_user_service.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.epharmacy.pharmacy_user_service.customerDto.request.RegisterDTO;
import com.epharmacy.pharmacy_user_service.entity.Customer;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


	
	@Autowired
	private MyUserDetailService userDetailService;
	
	@Autowired
	private JWTFliter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		return http.csrf(customizer->customizer.disable())
				.cors(cors->{})
				.authorizeHttpRequests(req->req
						
						.requestMatchers("/customer/register",
								          "/customer/login").permitAll()
						
						.anyRequest().authenticated())
				
				.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationprovider() {
		
		DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailService);
		
		provider.setPasswordEncoder(passwordEncoder());
		
		return provider;
	}
	
	@Bean
	public AuthenticationManager authManager(AuthenticationConfiguration config)throws Exception{
		return config.getAuthenticationManager();
		
	}
	@Bean
    public ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(RegisterDTO.class, Customer.class)
              .addMappings(mapping -> {
                  mapping.skip(Customer::setId);
              });

        return mapper;
    }
	

}

