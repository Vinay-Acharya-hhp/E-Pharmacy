package com.epharmacy.pharmacy_order_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.epharmacy.pharmacy_order_service.apiResponse.ApiResponse;



@RestControllerAdvice
public class GlobalExceptionHandler {


	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(OrderNotFoundException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				
				e.getMessage(),
				false,
				HttpStatus.BAD_REQUEST.value()
				);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<?>> handleException(Exception e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.INTERNAL_SERVER_ERROR.value()
				);
		return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(AddressNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleAddressNotFoundException(AddressNotFoundException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.NOT_FOUND.value()
				);
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
}
