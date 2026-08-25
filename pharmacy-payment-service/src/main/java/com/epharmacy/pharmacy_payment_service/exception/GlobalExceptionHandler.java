package com.epharmacy.pharmacy_payment_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.epharmacy.pharmacy_payment_service.apiresponse.ApiResponse;



@RestControllerAdvice
public class GlobalExceptionHandler {


	@ExceptionHandler(CardNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(CardNotFoundException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				
				e.getMessage(),
				false,
				HttpStatus.NOT_FOUND.value()
				);
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
}
