package com.taxi.userservice.web;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.userservice.security.JwtService;
import com.taxi.userservice.web.dto.TokenRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtService jwtService;

	@PostMapping("/token")
	public Map<String, String> token(@Valid @RequestBody TokenRequest body) {
		String token = jwtService.createToken(body.subject(), body.role());
		return Map.of("accessToken", token, "tokenType", "Bearer");
	}
}
