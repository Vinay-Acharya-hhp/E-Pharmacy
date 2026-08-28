package com.epharmacy.pharmacy_payment_service.exception;

public class OrderNotFoundException extends RuntimeException{
	public OrderNotFoundException(String message) {
		super(message);
	}

}
