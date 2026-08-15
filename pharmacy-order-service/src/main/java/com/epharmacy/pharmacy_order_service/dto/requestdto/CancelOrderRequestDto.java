package com.epharmacy.pharmacy_order_service.dto.requestdto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CancelOrderRequestDto {
	@NotBlank
	private String cancelReason;

}
