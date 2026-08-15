package com.epharmacy.pharmacy_order_service.dto.requestdto;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class PlaceOrderRequestDto {
	
	@NotNull
	private Long customerId;
	@NotNull
	private Long addressId;
	@NotNull
	@Positive
	private Double orderValueBeforeDiscount;

}
