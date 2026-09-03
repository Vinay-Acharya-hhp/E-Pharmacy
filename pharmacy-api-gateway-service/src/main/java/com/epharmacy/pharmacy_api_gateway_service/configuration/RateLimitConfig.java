package com.epharmacy.pharmacy_api_gateway_service.configuration;

import java.security.Principal;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;



@Configuration
public class RateLimitConfig {

	@Bean
	public KeyResolver userKeyResolver() {
		return exchange-> exchange.getPrincipal()
				.map(Principal::getName);
	}
	
	public KeyResolver ipKeyResolver() {
		return exchange->
		Mono.just(exchange.getRequest()
				          .getRemoteAddress()
				          .getAddress()
				          .getHostAddress());
	}
}
