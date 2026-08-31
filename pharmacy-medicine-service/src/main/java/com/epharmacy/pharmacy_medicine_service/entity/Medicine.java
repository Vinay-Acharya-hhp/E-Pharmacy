package com.epharmacy.pharmacy_medicine_service.entity;

import java.time.LocalDate;




import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="medicine")
@Entity
public class Medicine {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String medicineName;
	private String manufacturer;
	private String category;
	private LocalDate manufacturing_Date;
	private LocalDate expirey_Date;
	private Integer quantity;
	private double price;
	private int discountPercent;
	private String imageUrl;

}
