package com.epharmacy.pharmacy_user_service.customerDto.request;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import com.epharmacy.pharmacy_user_service.customerDto.response.AddressDTO;
import com.epharmacy.pharmacy_user_service.entity.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterDTO {
 
	

	private String customerName;
	private String customerEmailId;
	private String contactNumber;
	private String password;
	private Gender gender;
	@JsonFormat(pattern= "dd-MM-yyyy")
	private LocalDate dateOfBirth;
	
	private List<AddressDTO> address =new ArrayList<>();
}
