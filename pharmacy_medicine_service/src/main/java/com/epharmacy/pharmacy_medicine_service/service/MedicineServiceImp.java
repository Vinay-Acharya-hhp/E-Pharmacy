package com.epharmacy.pharmacy_medicine_service.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_medicine_service.dto.requestdto.MedicineRequestDTO;
import com.epharmacy.pharmacy_medicine_service.dto.responsedto.MedicineResponseDTO;
import com.epharmacy.pharmacy_medicine_service.entity.Medicine;
import com.epharmacy.pharmacy_medicine_service.exception.MedicineNotFoundException;
import com.epharmacy.pharmacy_medicine_service.repository.MedicineRepo;

import jakarta.transaction.Transactional;

@Service
public class MedicineServiceImp implements MedicineService {
	
	private MedicineRepo repo;
	
	private ModelMapper modelemapper;

	public MedicineServiceImp(MedicineRepo repo, ModelMapper modelemapper) {
		super();
		this.repo = repo;
		this.modelemapper = modelemapper;
	}

	@Override
	public MedicineResponseDTO addMedicine(MedicineRequestDTO medicineReq) {
		
		Medicine medicine=modelemapper.map(medicineReq, Medicine.class);
		Medicine save=repo.save(medicine);
		
		return modelemapper.map(save, MedicineResponseDTO.class);
	}

	@Override
	public Page<MedicineResponseDTO> getAll(int offset ,int size) {
		Page <Medicine> allMedicine = repo.findAll(PageRequest.of(offset, size));
		return allMedicine
				.map(medicine-> modelemapper.map(medicine,  MedicineResponseDTO.class));
				
	}

	@Override
	public Page <MedicineResponseDTO> getAllbycatogary(String catogery,int pageno,int size) {
		
		Page<Medicine> getbycatogory=repo.findByCategory(catogery,PageRequest.of(pageno, size));
		System.out.println( getbycatogory.getContent());
		return getbycatogory
				.map(medicine-> modelemapper.map(medicine,  MedicineResponseDTO.class));
				
	}

	@Override
	public MedicineResponseDTO getById(Long id) {
		Medicine medicine =repo.findById(id).orElseThrow(()-> new MedicineNotFoundException("NOT FOUND"));
		                   
		
		return modelemapper.map(medicine, MedicineResponseDTO.class);
	}

	@Override
	@Transactional
	public void updateStock(Long medicineId, Integer orderedQuantity) {

	    if (orderedQuantity == null || orderedQuantity <= 0) {
	        throw new IllegalArgumentException(
	                "Ordered quantity must be greater than 0");
	    }

	    Medicine medicine = repo.findById(medicineId)
	            .orElseThrow(() ->
	                    new MedicineNotFoundException(
	                            "Medicine with id " + medicineId + " not found"));

	    if (medicine.getQuantity() < orderedQuantity) {
	        throw new IllegalArgumentException(
	                "Insufficient stock. Available quantity: "
	                        + medicine.getQuantity());
	    }

	    medicine.setQuantity(
	            medicine.getQuantity() - orderedQuantity
	    );

	    repo.save(medicine);
	}

	@Override
	public Page<MedicineResponseDTO> serach(String medicineName, int number, int size) {
		Page<Medicine> serachmedicine=repo.findByMedicineNameContainingIgnoreCase(medicineName,PageRequest.of(number, size));
		//System.out.println( getbycatogory.getContent());
		return serachmedicine
				.map(medicine-> modelemapper.map(medicine,  MedicineResponseDTO.class));
		
	}
	

}
