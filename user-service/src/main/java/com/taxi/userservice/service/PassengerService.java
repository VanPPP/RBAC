package com.taxi.userservice.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi.common.dto.PassengerDto;
import com.taxi.userservice.entity.PassengerEntity;
import com.taxi.userservice.repository.PassengerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PassengerService {

	private final PassengerRepository passengerRepository;

	@Transactional
	public PassengerDto register(String name, String email, String phone) {
		PassengerEntity e = PassengerEntity.builder()
				.name(name)
				.email(email)
				.phone(phone)
				.createdAt(Instant.now())
				.build();
		passengerRepository.save(e);
		return toDto(e);
	}

	@Transactional(readOnly = true)
	public Optional<PassengerDto> findById(Long id) {
		return passengerRepository.findById(id).map(PassengerService::toDto);
	}

	private static PassengerDto toDto(PassengerEntity e) {
		return PassengerDto.builder()
				.id(e.getId())
				.name(e.getName())
				.email(e.getEmail())
				.phone(e.getPhone())
				.build();
	}
}
