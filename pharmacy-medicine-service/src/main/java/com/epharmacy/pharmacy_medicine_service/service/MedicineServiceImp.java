package com.epharmacy.pharmacy_medicine_service.service;

import java.util.List;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_medicine_service.dto.requestdto.MedicineRequestDTO;
import com.epharmacy.pharmacy_medicine_service.dto.responsedto.MedicinePageResponseDTO;
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
    
	@Caching(evict= {
			@CacheEvict(value="medicineCatogary",allEntries=true),
			@CacheEvict(value="medicinePages",allEntries=true),
			@CacheEvict(value="medicinesearch",allEntries=true)
			
	})
	@Override
	public MedicineResponseDTO addMedicine(MedicineRequestDTO medicineReq) {
		
		Medicine medicine=modelemapper.map(medicineReq, Medicine.class);
		
		Medicine save=repo.save(medicine);
		
		return modelemapper.map(save, MedicineResponseDTO.class);
	}
    
	@Cacheable(value = "medicinePages", key = "#offset + '-' + #size")
	public MedicinePageResponseDTO getAll(int offset, int size) {

	    Page<Medicine> page =
	            repo.findAll(PageRequest.of(offset, size));

	    List<MedicineResponseDTO> content = page.getContent()
	            .stream()
	            .map(medicine ->
	                    modelemapper.map(medicine, MedicineResponseDTO.class))
	            .toList();

	    MedicinePageResponseDTO response = new MedicinePageResponseDTO();

	    response.setContent(content);
	    response.setPage(page.getNumber());
	    response.setSize(page.getSize());
	    response.setTotalElements(page.getTotalElements());
	    response.setTotalPages(page.getTotalPages());

	    return response;
	}
    
	@Cacheable(
		    value = "medicineCatogary",
		    key = "#catogery + '-' + #pageno + '-' + #size"
		)
		@Override
		public MedicinePageResponseDTO getAllbycatogary(
		        String catogery, int pageno, int size) {

		    Page<Medicine> getbycatogory =
		            repo.findByCategory(
		                    catogery,
		                    PageRequest.of(pageno, size)
		            );

		    List<MedicineResponseDTO> content =
		            getbycatogory.getContent()
		                    .stream()
		                    .map(medicine ->
		                            modelemapper.map(
		                                    medicine,
		                                    MedicineResponseDTO.class
		                            ))
		                    .toList();

		    MedicinePageResponseDTO response =
		            new MedicinePageResponseDTO();

		    response.setContent(content);
		    response.setPage(getbycatogory.getNumber());
		    response.setSize(getbycatogory.getSize());
		    response.setTotalElements(getbycatogory.getTotalElements());
		    response.setTotalPages(getbycatogory.getTotalPages());

		    return response;
		}

	@Cacheable(value= "medicinesbyid" , key="#id")
	@Override
	public MedicineResponseDTO getById(Long id) {
		Medicine medicine =repo.findById(id).orElseThrow(()-> new MedicineNotFoundException("NOT FOUND"));
		                   
		
		return modelemapper.map(medicine, MedicineResponseDTO.class);
	}

//	@Caching(put= {
//			@CachePut(value="medicinesbyid",key="#medicineId")},
//			evict= {
//					@CacheEvict(value="medicineCatogary",allEntries=true),
//					@CacheEvict(value="medicinePages",allEntries=true),
//					@CacheEvict(value="medicinesearch",allEntries=true)
//					
//			})
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
	
	
	@Cacheable(value="medicinesearch",key="#medicineName + '-' + #number + '-' + #size")
	@Override
	public MedicinePageResponseDTO search(String medicineName, int number, int size) {
		Page<Medicine> serachmedicine=repo.findByMedicineNameContainingIgnoreCase(medicineName,PageRequest.of(number, size));
		System.out.println(serachmedicine);
	    List<MedicineResponseDTO> content =
	    		serachmedicine.getContent()
	                    .stream()
	                    .map(medicine ->
	                            modelemapper.map(
	                                    medicine,
	                                    MedicineResponseDTO.class
	                            ))
	                    .toList();
	    System.out.println(content);
		 MedicinePageResponseDTO response = new MedicinePageResponseDTO();

		    response.setContent(content);
		    response.setPage(serachmedicine.getNumber());
		    response.setSize(serachmedicine.getSize());
		    response.setTotalElements(serachmedicine.getTotalElements());
		    response.setTotalPages(serachmedicine.getTotalPages());

		    return response;
	}
	

}
