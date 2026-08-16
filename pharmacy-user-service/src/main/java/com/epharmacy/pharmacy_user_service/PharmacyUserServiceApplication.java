package com.epharmacy.pharmacy_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PharmacyUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmacyUserServiceApplication.class, args);
	}

}
