package com.epharmacy.pharmacy_user_service.customerDto.request;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import com.epharmacy.pharmacy_user_service.customerDto.response.AddressDTO;
import com.epharmacy.pharmacy_user_service.entity.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
 
	
    @NotBlank(message="First name is required")
    @Size(min=2,max=50,message="First Name must be between 2 ans 50 characters")
	private String customerName;
    @NotBlank(message="Email is required")
    @Email(message="Enter a valid email address")
	private String customerEmailId;
    @NotBlank(message="Contact number is required")
    @Pattern(
    		regexp="^[6-9][0-9]{9}$",
    		message="Contact number must be a valid 10 digit")
	private String contactNumber;
    @NotBlank(message="Password is required")
    @Size(min=6,max=20,message="Password must be between 6 and 20 characters")
	private String password;
    @NotBlank(message="Select your gender")
	private Gender gender;
    @NotNull(message="Date of birth is required")
    @Past(message="Date of birth must be in the past")
    @PastOrPresent(message="Date of birth cannot be in the future")
	@JsonFormat(pattern= "dd-MM-yyyy")
	private LocalDate dateOfBirth;
	
	private List<AddressDTO> address =new ArrayList<>();
}
