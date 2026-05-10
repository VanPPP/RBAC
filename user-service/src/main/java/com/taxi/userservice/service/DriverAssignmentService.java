package com.taxi.userservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi.common.domain.DriverStatus;
import com.taxi.common.dto.DriverDto;
import com.taxi.userservice.entity.DriverEntity;
import com.taxi.userservice.repository.DriverRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverAssignmentService {

	private final DriverRepository driverRepository;

	@Transactional
	@CacheEvict(cacheNames = "availableDrivers", allEntries = true)
	public Optional<DriverDto> acquireAvailableDriver() {
		List<Long> ids = driverRepository.lockOneAvailableDriverId();
		if (ids.isEmpty()) {
			return Optional.empty();
		}
		Long id = ids.get(0);
		DriverEntity driver = driverRepository.findById(id).orElseThrow();
		driver.setStatus(DriverStatus.BUSY);
		return Optional.of(toDto(driver));
	}

	private static DriverDto toDto(DriverEntity d) {
		return DriverDto.builder()
				.id(d.getId())
				.name(d.getName())
				.email(d.getEmail())
				.phone(d.getPhone())
				.licenseNumber(d.getLicenseNumber())
				.status(d.getStatus().name())
				.build();
	}
}
