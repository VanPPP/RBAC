package com.taxi.userservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.common.dto.PassengerDto;
import com.taxi.userservice.service.PassengerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalPassengerController {

	private final PassengerService passengerService;

	@GetMapping("/passengers/{id}")
	public ResponseEntity<PassengerDto> getForService(@PathVariable Long id) {
		return passengerService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
