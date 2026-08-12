package com.epharmacy.pharmacy_medicine_service.configration;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.epharmacy.pharmacy_medicine_service.dto.requestdto.MedicineRequestDTO;
import com.epharmacy.pharmacy_medicine_service.entity.Medicine;



@Configuration
public class ModelMapperConfiguration {

	@Bean
    public ModelMapper modelMapper() {

        return new ModelMapper();
    }
}
