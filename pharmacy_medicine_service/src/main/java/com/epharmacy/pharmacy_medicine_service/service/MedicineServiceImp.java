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
	

}
