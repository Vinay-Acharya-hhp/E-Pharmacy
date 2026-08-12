package com.epharmacy.pharmacy_medicine_service.apiResponse;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
//@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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



		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		

}
