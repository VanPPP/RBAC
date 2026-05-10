package com.taxi.userservice.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InternalApiKeyFilter extends OncePerRequestFilter {

	public static final String INTERNAL_HEADER = "X-Internal-Api-Key";

	@Value("${taxi.internal.api-key}")
	private String expectedKey;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		if (!request.getRequestURI().startsWith("/internal")) {
			filterChain.doFilter(request, response);
			return;
		}
		String key = request.getHeader(INTERNAL_HEADER);
		if (key == null || !key.equals(expectedKey)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		return !request.getRequestURI().startsWith("/internal");
	}
}
