package com.epharmacy.pharmacy_payment_service.apiresponse;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

	private T data;
	
	private boolean success;
	
	private int status ;
	
	private LocalDateTime timestamp;

	public ApiResponse( T data, boolean success, int status) {
		super();
		
		this.data = data;
		this.success = success;
		this.status = status;
		this.timestamp = LocalDateTime.now();
	}
  
}
