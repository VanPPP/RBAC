package com.taxi.userservice.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey key;
	private final long ttlMs;

	public JwtService(
			@Value("${taxi.jwt.secret}") String secret,
			@Value("${taxi.jwt.expiration-ms:86400000}") long ttlMs) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.ttlMs = ttlMs;
	}

	public String createToken(String subject, String role) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.subject(subject)
				.claim("role", role)
				.issuedAt(new Date(now))
				.expiration(new Date(now + ttlMs))
				.signWith(key)
				.compact();
	}

	public ParsedToken parse(String token) {
		var parsed = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return new ParsedToken(parsed.getSubject(), parsed.get("role", String.class));
	}

	public record ParsedToken(String subject, String role) {
	}
}
