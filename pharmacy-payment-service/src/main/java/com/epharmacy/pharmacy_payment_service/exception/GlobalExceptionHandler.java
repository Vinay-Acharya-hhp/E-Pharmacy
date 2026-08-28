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
	
	

	@ExceptionHandler(InvalidAmountException.class)
	public ResponseEntity<ApiResponse<?>> handleInvalidAmountException(InvalidAmountException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				
				e.getMessage(),
				false,
				HttpStatus.PAYMENT_REQUIRED.value()
				);
		return new ResponseEntity<>(response,HttpStatus.PAYMENT_REQUIRED);
	}
	
	@ExceptionHandler(NullValueException.class)
	public ResponseEntity<ApiResponse<?>> handleNullValueException(NullValueException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				
				e.getMessage(),
				false,
				HttpStatus.BAD_REQUEST.value()
				);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleOrderNotFoundException(OrderNotFoundException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				
				e.getMessage(),
				false,
				HttpStatus.NOT_FOUND.value()
				);
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
}
