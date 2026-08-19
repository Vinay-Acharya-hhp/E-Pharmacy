package com.epharmacy.pharmacy_order_service.dto.responsedto;


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
  

	
	private String addressName;
	private String addressLine1;
	private String addressLine2;
	private String area;
	private String city;
	private String state;
	private String pincode;
}
