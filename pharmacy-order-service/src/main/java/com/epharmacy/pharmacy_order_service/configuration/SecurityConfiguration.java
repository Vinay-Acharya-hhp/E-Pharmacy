package com.epharmacy.pharmacy_order_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

//	                        // GET medicine is public
//	                        .requestMatchers(
//	                                HttpMethod.GET,
//	                                "/cart/**"
//	                        ).permitAll()

	                        // Everything else requires authentication
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
	}


