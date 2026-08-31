package com.epharmacy.pharmacy_medicine_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epharmacy.pharmacy_medicine_service.entity.Medicine;

@Repository
public interface MedicineRepo extends JpaRepository<Medicine,Long>{
	
   Page<Medicine>findByCategory(String category,Pageable pageable);
   Page<Medicine> findByMedicineNameContainingIgnoreCase(String medicineName,Pageable pageable);

	//List<Medicine> findByCategory(Sort by);
   
}
