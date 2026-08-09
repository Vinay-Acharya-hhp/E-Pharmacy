package com.epharmacy.pharmacy_user_service.entity;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="address")
@Entity
public class Address {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String addressName;
	private String addressLine1;
	private String addressLine2;
	private String area;
	private String city;
	private String state;
	private String pincode;
	@ManyToOne
	@JoinColumn(name="customer_id")
	private Customer customer;
	

}
