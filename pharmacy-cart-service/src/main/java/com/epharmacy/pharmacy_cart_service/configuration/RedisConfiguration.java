package com.epharmacy.pharmacy_cart_service.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = new ObjectMapper();

        GenericJacksonJsonRedisSerializer serializer =
                new GenericJacksonJsonRedisSerializer(objectMapper);

        RedisCacheConfiguration cartListConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer)
                        );

        Map<String, RedisCacheConfiguration> cacheConfigurations =
                new HashMap<>();

        cacheConfigurations.put("ordersByCustomer", cartListConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cartListConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
