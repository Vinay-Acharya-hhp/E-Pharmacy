package com.epharmacy.pharmacy_user_service.customerDto.request;



import com.epharmacy.pharmacy_user_service.entity.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerRequestDTO {

	private String customerName;
	private String customerEmailId;
	private String contactNumber;
	private Gender gender;
	
	
}
