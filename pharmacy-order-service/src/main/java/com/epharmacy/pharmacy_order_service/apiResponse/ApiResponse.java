package com.epharmacy.pharmacy_order_service.apiResponse;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
	
	private T data;
	private boolean success ;
	private int status;
	private LocalDateTime timestamp;
	

	public ApiResponse( T data, boolean success, int status) {
		super();
		
		this.data = data;
		this.success = success;
		this.status = status;
		this.timestamp = LocalDateTime.now();
	}
}
