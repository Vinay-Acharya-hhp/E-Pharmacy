package com.epharmacy.pharmacy_cart_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.epharmacy.pharmacy_cart_service.apiresponse.ApiResponse;



@RestControllerAdvice
public class GlobalExceptionHandler {
	

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
	

	@ExceptionHandler(MedicineAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<?>> handleMedicineAlreadyExistsException(Exception e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.CONFLICT.value()
				);
		return new ResponseEntity<>(response,HttpStatus.CONFLICT);
	}
	

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(Exception e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.BAD_REQUEST.value()
				);
		return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MedicineNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleMedicineNotFoundException(Exception e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.NOT_FOUND.value()
				);
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(CartIsEmptyException.class)
	public ResponseEntity<ApiResponse<?>> handleCartIsEmptyException(Exception e)
	{
		
		ApiResponse<?> response=new ApiResponse<>(
				e.getMessage(),
				false,
				HttpStatus.NOT_FOUND.value()
				);
		return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
	}
	

	
}

