package com.taxi.userservice.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi.common.domain.DriverStatus;
import com.taxi.common.dto.DriverDto;
import com.taxi.userservice.entity.DriverEntity;
import com.taxi.userservice.repository.DriverRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverService {

	private final DriverRepository driverRepository;

	@Transactional
	@CacheEvict(cacheNames = "availableDrivers", allEntries = true)
	public DriverDto register(String name, String email, String phone, String licenseNumber) {
		DriverEntity e = DriverEntity.builder()
				.name(name)
				.email(email)
				.phone(phone)
				.licenseNumber(licenseNumber)
				.status(DriverStatus.AVAILABLE)
				.createdAt(Instant.now())
				.build();
		driverRepository.save(e);
		return toDto(e);
	}

	@Transactional(readOnly = true)
	public Optional<DriverDto> findById(Long id) {
		return driverRepository.findById(id).map(DriverService::toDto);
	}

	@Transactional
	@CacheEvict(cacheNames = "availableDrivers", allEntries = true)
	public Optional<DriverDto> updateStatus(Long id, DriverStatus status) {
		return driverRepository.findById(id)
				.map(d -> {
					d.setStatus(status);
					return toDto(d);
				});
	}

	@Cacheable(cacheNames = "availableDrivers", key = "'all'")
	@Transactional(readOnly = true)
	public List<DriverDto> listAvailableDrivers() {
		return driverRepository.findAllByStatusOrderById(DriverStatus.AVAILABLE).stream()
				.map(DriverService::toDto)
				.toList();
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
