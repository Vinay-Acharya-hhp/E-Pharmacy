package com.epharmacy.pharmacy_medicine_service.dto.requestdto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Data
@AllArgsConstructor
public class MedicineRequestDTO {
	private String medicineName;
	private String manufacturer;
	private String category;
	@JsonFormat(pattern= "dd-MM-yyyy")
	private LocalDate manufacturing_Date;
	@JsonFormat(pattern= "dd-MM-yyyy")
	private LocalDate expirey_Date;
	private Double price;
	private Integer discountPercent;
	private Integer quantity;
	private String imageUrl;
}
