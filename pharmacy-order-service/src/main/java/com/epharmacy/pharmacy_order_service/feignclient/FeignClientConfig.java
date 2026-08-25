package com.epharmacy.pharmacy_order_service.feignclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfig {
	
	

	    @Bean
	    public RequestInterceptor requestInterceptor(
	            HttpServletRequest request) {

	        return requestTemplate -> {

	            String authorization =
	                    request.getHeader("Authorization");
	            
	            
	            if (authorization != null &&
	                !authorization.isBlank()) {

	                requestTemplate.header(
	                        "Authorization",
	                        authorization
	                );
	            }
	        };
	    }
	}

