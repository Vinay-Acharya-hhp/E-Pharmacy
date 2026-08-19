package com.epharmacy.pharmacy_user_service.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message){
		super(message);
	}

}
