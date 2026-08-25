package com.epharmacy.pharmacy_payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PharmacyPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmacyPaymentServiceApplication.class, args);
	}

}
