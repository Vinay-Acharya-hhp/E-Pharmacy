package com.epharmacy.pharmacy_medicine_service.configration;


import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.epharmacy.pharmacy_medicine_service.dto.responsedto.MedicinePageResponseDTO;
import com.epharmacy.pharmacy_medicine_service.dto.responsedto.MedicineResponseDTO;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {

        // Serializer for Get By ID
        JacksonJsonRedisSerializer<MedicineResponseDTO> medicineSerializer =
                new JacksonJsonRedisSerializer<>(MedicineResponseDTO.class);

        RedisCacheConfiguration medicineConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(medicineSerializer)
                        );

        // Serializer for Get All / Pagination
        JacksonJsonRedisSerializer<MedicinePageResponseDTO> pageSerializer =
                new JacksonJsonRedisSerializer<>(MedicinePageResponseDTO.class);

        RedisCacheConfiguration pageConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(pageSerializer)
                        );

        Map<String, RedisCacheConfiguration> cacheConfigurations =
                new HashMap<>();

        cacheConfigurations.put("medicineById", medicineConfig);
        cacheConfigurations.put("medicinePages", pageConfig);
        cacheConfigurations.put("medicineCatogary", pageConfig);
        cacheConfigurations.put("medicinesearch", pageConfig);
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(medicineConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}