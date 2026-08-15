package com.epharmacy.pharmacy_order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="order_items")
public class OrderItem {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long orderItemId;
  private Long orderId;
  private Long medicineId;
  private String medicineName;
  private Double price;
  private Double discount;
  private Integer quantity;
  private Double subtotal;
  
}
