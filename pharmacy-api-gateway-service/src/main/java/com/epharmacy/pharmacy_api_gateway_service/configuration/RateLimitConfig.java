package com.epharmacy.pharmacy_api_gateway_service.configuration;

import java.security.Principal;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    // @Primary: RequestRateLimiterGatewayFilterFactory autowires one unqualified
    // KeyResolver as its default. With two KeyResolver beans and no @Primary the
    // gateway context fails to start. Explicit "#{@userKeyResolver}" /
    // "#{@ipKeyResolver}" references in application.yaml still resolve by name.
    @Primary
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName)
                // Fall back to IP so unauthenticated requests are still
                // rate-limited instead of sharing an empty/null key.
                .switchIfEmpty(Mono.fromSupplier(() -> resolveIp(exchange)));
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(resolveIp(exchange));
    }

    // Behind CloudFront + ALB the socket address is the ALB's private IP, so
    // prefer the first X-Forwarded-For entry (the original client).
    // Acceptable here because the ALB only accepts traffic from CloudFront.
    private String resolveIp(ServerWebExchange exchange) {
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        var remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress == null || remoteAddress.getAddress() == null) {
            return "unknown";
        }
        return remoteAddress.getAddress().getHostAddress();
    }
}
