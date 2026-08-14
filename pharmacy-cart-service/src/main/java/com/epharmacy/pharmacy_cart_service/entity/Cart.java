package com.epharmacy.pharmacy_cart_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name="cart",uniqueConstraints= {
		@UniqueConstraint(columnNames= {"customer_id","medicine_id"})
})
public class Cart {
	 
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long cartId;
	@Column(name="customer_id",nullable=false)
	private Long customerId;
	@Column(name="medicine_id",nullable=false)
	private Long medicineId;
	@Column(nullable=false)
	private Integer quantity;
}
