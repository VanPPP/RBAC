package com.taxi.userservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.common.dto.DriverDto;
import com.taxi.userservice.service.DriverAssignmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalDriverController {

	private final DriverAssignmentService driverAssignmentService;

	@PostMapping("/drivers/acquire")
	public ResponseEntity<DriverDto> acquireFreeDriver() {
		return driverAssignmentService.acquireAvailableDriver()
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
