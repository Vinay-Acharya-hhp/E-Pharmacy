package com.epharmacy.pharmacy_medicine_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.epharmacy.pharmacy_medicine_service.apiResponse.ApiResponse;



@RestControllerAdvice
public class GlobalExceptionHandlar {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<?>> handleMethodArgNotValidException(MethodArgumentNotValidException e)
	{
		String error=e.getBindingResult()
				.getFieldError()
				.getDefaultMessage();
		
		ApiResponse<?> response=new ApiResponse<>(
			    e.getFieldValue(error),
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
	
	@ExceptionHandler(MedicineNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleException(MedicineNotFoundException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.NOT_FOUND.value()
				);
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
	
}
