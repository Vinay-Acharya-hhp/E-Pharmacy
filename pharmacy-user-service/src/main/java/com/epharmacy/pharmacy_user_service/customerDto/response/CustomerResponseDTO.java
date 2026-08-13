package com.epharmacy.pharmacy_user_service.customerDto.response;

import java.time.LocalDate;



import com.epharmacy.pharmacy_user_service.entity.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerResponseDTO {

	private String customerName;
	private String customerEmailId;
	private String contactNumber;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	private LocalDate dateOfBirth;
	
}
