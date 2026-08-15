package com.epharmacy.pharmacy_cart_service.configuration;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
	public class JWTFilter extends OncePerRequestFilter {

	    @Autowired
	    private JWTService jwtService;

	    @Override
	    protected void doFilterInternal(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            FilterChain filterChain)
	            throws ServletException, IOException {

	        String authHeader =
	                request.getHeader("Authorization");

	        String token = null;
	        String email = null;

	        try {

	            if (authHeader != null &&
	                authHeader.startsWith("Bearer ")) {

	                token = authHeader.substring(7);

	                email = jwtService.extractUserName(token);

	                System.out.println("JWT received");
	                System.out.println("Customer: " + email);
	            }

	            if (email != null &&
	                SecurityContextHolder
	                    .getContext()
	                    .getAuthentication() == null) {

	                if (jwtService.validateToken(token)) {

	                    UsernamePasswordAuthenticationToken authToken =
	                            new UsernamePasswordAuthenticationToken(
	                                    email,
	                                    null,
	                                    Collections.emptyList()
	                            );

	                    authToken.setDetails(
	                            new WebAuthenticationDetailsSource()
	                                    .buildDetails(request)
	                    );

	                    SecurityContextHolder
	                            .getContext()
	                            .setAuthentication(authToken);

	                    System.out.println("JWT VALID");
	                }
	            }

	        } catch (Exception e) {

	            System.out.println(
	                    "JWT Error: " + e.getMessage()
	            );
	        }

	        filterChain.doFilter(request, response);
	    }
	}


