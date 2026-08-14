package com.epharmacy.pharmacy_cart_service.exception;

public class CartIsEmptyException extends RuntimeException{
	public CartIsEmptyException(String message) {
		super(message);
	}
	

}
