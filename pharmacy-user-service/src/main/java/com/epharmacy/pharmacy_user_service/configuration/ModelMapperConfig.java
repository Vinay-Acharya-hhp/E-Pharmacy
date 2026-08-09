package com.epharmacy.pharmacy_user_service.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.epharmacy.pharmacy_user_service.customerDto.RegisterDTO;
import com.epharmacy.pharmacy_user_service.entity.Customer;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(RegisterDTO.class, Customer.class)
              .addMappings(mapping -> {
                  mapping.skip(Customer::setId);
              });

        return mapper;
    }
}
