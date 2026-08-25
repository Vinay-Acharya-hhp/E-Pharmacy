package com.epharmacy.pharmacy_medicine_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epharmacy.pharmacy_medicine_service.dto.requestdto.MedicineRequestDTO;
import com.epharmacy.pharmacy_medicine_service.dto.responsedto.MedicineResponseDTO;

public interface MedicineService {
	
	MedicineResponseDTO addMedicine(MedicineRequestDTO medicineReq);
	Page <MedicineResponseDTO> getAll(int number,int size);
	
	Page <MedicineResponseDTO> getAllbycatogary(String catogery,int number,int size);
	MedicineResponseDTO getById(Long id);
    void updateStock(Long medicineId , Integer quantity);
}