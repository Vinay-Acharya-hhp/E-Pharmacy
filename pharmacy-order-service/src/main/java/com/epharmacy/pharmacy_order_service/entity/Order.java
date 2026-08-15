package com.epharmacy.pharmacy_order_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long orderId;
	private Long customerId;
	private Long addressId;
	private Double ordervalueBeforeDiscount;
	private Double discountAmount;
	private Double healthCoinUsed;
	private Double finalAmount;
	private LocalDateTime orderDate;
	private LocalDate expectedDeliveryDate;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	@Enumerated(EnumType.STRING)
	private DeliveryStatus deliveryStatus;

}
