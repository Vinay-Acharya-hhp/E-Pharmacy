package com.epharmacy.pharmacy_user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.epharmacy.pharmacy_user_service.apiResponse.ApiResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {
	

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(UserNotFoundException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				
				e.getMessage(),
				false,
				HttpStatus.BAD_REQUEST.value()
				);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	
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
	
	
	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<?>> handleResourceAlreadyExitsException(ResourceAlreadyExistsException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.CONFLICT.value()
				);
		return new ResponseEntity<>(response,HttpStatus.CONFLICT);
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
	
	@ExceptionHandler(PasswordException.class)
	public ResponseEntity<ApiResponse<?>> handlePasswordException(PasswordException e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.BAD_REQUEST.value()
				);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	
	
}
