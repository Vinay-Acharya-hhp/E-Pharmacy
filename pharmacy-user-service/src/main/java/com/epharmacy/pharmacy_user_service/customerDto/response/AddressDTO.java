package com.epharmacy.pharmacy_user_service.customerDto.response;



import jakarta.validation.constraints.NotBlank;
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
public class AddressDTO {
  

	@NotBlank(message="address is required")
	private String addressName;
	private String addressLine1;
	private String addressLine2;
	private String area;
	private String city;
	@NotBlank(message="select state")
	private String state;
	@NotBlank(message="Pincode is required")
	@Size(min=6,max=6)
	private String pincode;
	
	
	
}
