package com.taxi.userservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.common.dto.PassengerDto;
import com.taxi.userservice.service.PassengerService;
import com.taxi.userservice.web.dto.CreatePassengerRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

	private final PassengerService passengerService;

	@PostMapping
	public ResponseEntity<PassengerDto> register(@Valid @RequestBody CreatePassengerRequest body) {
		PassengerDto dto = passengerService.register(body.name(), body.email(), body.phone());
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PassengerDto> get(@PathVariable Long id) {
		return passengerService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
