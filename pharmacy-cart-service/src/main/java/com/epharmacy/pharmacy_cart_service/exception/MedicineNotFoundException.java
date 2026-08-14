package com.epharmacy.pharmacy_cart_service.exception;

public class MedicineNotFoundException extends RuntimeException{
	public MedicineNotFoundException(String message) {
		super(message);
	}
}
