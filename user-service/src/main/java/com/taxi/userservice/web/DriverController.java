package com.taxi.userservice.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.common.domain.DriverStatus;
import com.taxi.common.dto.DriverDto;
import com.taxi.userservice.service.DriverService;
import com.taxi.userservice.web.dto.CreateDriverRequest;
import com.taxi.userservice.web.dto.UpdateDriverStatusRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

	private final DriverService driverService;

	@PostMapping
	public ResponseEntity<DriverDto> register(@Valid @RequestBody CreateDriverRequest body) {
		DriverDto dto = driverService.register(body.name(), body.email(), body.phone(), body.licenseNumber());
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@GetMapping("/{id:\\d+}")
	public ResponseEntity<DriverDto> get(@PathVariable Long id) {
		return driverService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PatchMapping("/{id:\\d+}/status")
	public ResponseEntity<DriverDto> patchStatus(
			@PathVariable Long id,
			@Valid @RequestBody UpdateDriverStatusRequest body) {
		DriverStatus status = DriverStatus.valueOf(body.status());
		return driverService.updateStatus(id, status)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Список свободных водителей (кэш Redis через Spring Cache).
	 */
	@GetMapping("/available")
	public List<DriverDto> available() {
		return driverService.listAvailableDrivers();
	}
}
