package com.epharmacy.pharmacy_cart_service.dto.responsedto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicineResponseDTO {
	private Long id;
	private String medicineName;
	private String manufacturer;
	private String category;
	private LocalDate manufacturing_Date;
	private LocalDate expirey_Date;
	private Double price;
	private Integer discountPercent;
	private String imageUrl;
}
