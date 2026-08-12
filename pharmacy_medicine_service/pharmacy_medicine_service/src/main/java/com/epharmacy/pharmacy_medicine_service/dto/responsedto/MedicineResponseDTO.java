package com.epharmacy.pharmacy_medicine_service.dto.responsedto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Data
@AllArgsConstructor
public class MedicineResponseDTO {
	private Long id;
	private String medicineName;
	private String manufacturer;
	private String category;
	private LocalDate manufacturing_Date;
	private LocalDate expirey_Date;
	private double price;
	private int discountPercent;
}
