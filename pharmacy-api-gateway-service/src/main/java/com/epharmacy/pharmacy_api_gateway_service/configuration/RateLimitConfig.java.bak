package com.epharmacy.pharmacy_api_gateway_service.configuration;

import java.security.Principal;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import reactor.core.publisher.Mono;



@Configuration
public class RateLimitConfig {

	// Marked @Primary because Spring Cloud Gateway's
		// RequestRateLimiterGatewayFilterFactory autowires a single, unqualified
		// KeyResolver as its default (used for any route that doesn't specify
		// key-resolver explicitly). With two @Bean KeyResolvers and no @Primary,
		// that autowiring is ambiguous and the whole gateway context fails to
		// start. This does NOT affect the explicit "#{@userKeyResolver}" /
		// "#{@ipKeyResolver}" SpEL references in application.yaml — those still
		// resolve by bean name regardless of which one is primary.
		@Primary
		@Bean
		public KeyResolver userKeyResolver() {
			return exchange-> exchange.getPrincipal()
					.map(Principal::getName)
					// Fall back to IP so unauthenticated requests are still
					// rate-limited instead of all sharing an empty/null key.
					.switchIfEmpty(Mono.just(resolveIp(exchange)));
		}

		@Bean
		public KeyResolver ipKeyResolver() {
			return exchange -> Mono.just(resolveIp(exchange));
		}

		private String resolveIp(org.springframework.web.server.ServerWebExchange exchange) {
			var remoteAddress = exchange.getRequest().getRemoteAddress();
			if (remoteAddress == null || remoteAddress.getAddress() == null) {
				return "unknown";
			}
			return remoteAddress.getAddress().getHostAddress();
		}
	}