package com.epharmacy.pharmacy_order_service.dto.requestdto;



import java.util.List;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequestDto {
	
	 
	 private AddressRequestDto deliveryAddress;
	
	

}
