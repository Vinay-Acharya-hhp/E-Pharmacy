package com.epharmacy.pharmacy_payment_service.exception;

public class CardNotFoundException extends RuntimeException{
   public CardNotFoundException(String message) {
	   super(message);
   }
}
