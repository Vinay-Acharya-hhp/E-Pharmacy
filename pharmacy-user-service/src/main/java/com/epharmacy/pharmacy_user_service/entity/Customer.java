package com.epharmacy.pharmacy_user_service.entity;

import java.time.LocalDate;



import java.util.ArrayList;
import java.util.List;



import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

//import com.epharmacy.pharmacy_user_service.entity.Address;
//import com.fasterxml.jackson.annotation.JsonFormat;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="customer")
@Entity
public class Customer {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String customerName;
	private String customerEmailId;
	private String contactNumber;
	private String password;
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@Column(name= "date_of_birth")
	private LocalDate dateOfBirth;
	@OneToMany(mappedBy= "customer", cascade= CascadeType.ALL,orphanRemoval=true)
	private List<Address> address =new ArrayList<>();
	
	
	

}
