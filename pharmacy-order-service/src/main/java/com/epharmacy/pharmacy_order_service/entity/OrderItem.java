package com.epharmacy.pharmacy_order_service.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;
  private Long medicineId;
  private String medicineName;
  private  double  price;
  
  private Integer quantity;
  
    
  
}
