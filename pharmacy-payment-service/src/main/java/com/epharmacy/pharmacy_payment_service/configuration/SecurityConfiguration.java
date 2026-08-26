package com.epharmacy.pharmacy_payment_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
		

		    @Autowired
		    private JWTFilter jwtFilter;

		    @Bean
		    public SecurityFilterChain securityFilterChain(
		            HttpSecurity http) throws Exception {

		        return http
		                .csrf(customizer -> customizer.disable())

		                .cors(cors -> {})

		                .authorizeHttpRequests(req -> req

		                       
		                        .requestMatchers(
		                               
		                                "/payment/**"
		                        ).authenticated()

		                      
		                        .anyRequest().authenticated()
		                )

		                .sessionManagement(session ->
		                        session.sessionCreationPolicy(
		                                SessionCreationPolicy.STATELESS
		                        )
		                )

		                .addFilterBefore(
		                        jwtFilter,
		                        UsernamePasswordAuthenticationFilter.class
		                )

		                .build();
		    }
		    
		    @Bean
		    public AuthenticationManager authenticationManager() {
		        return authentication -> {
		            throw new BadCredentialsException("Authentication not supported");
		        };
		    }
		}


