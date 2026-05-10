package com.taxi.userservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.common.domain.DriverStatus;
import com.taxi.common.dto.DriverDto;
import com.taxi.userservice.service.DriverService;
import com.taxi.userservice.web.dto.UpdateDriverStatusRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalDriverStatusController {

	private final DriverService driverService;

	@PatchMapping("/drivers/{id}/status")
	public ResponseEntity<DriverDto> patchStatus(
			@PathVariable Long id,
			@Valid @RequestBody UpdateDriverStatusRequest body) {
		DriverStatus status = DriverStatus.valueOf(body.status());
		return driverService.updateStatus(id, status)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
