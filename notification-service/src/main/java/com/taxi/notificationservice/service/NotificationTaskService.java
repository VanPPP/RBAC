package com.taxi.notificationservice.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi.common.domain.NotificationTaskStatus;
import com.taxi.common.domain.RecipientType;
import com.taxi.notificationservice.entity.NotificationTaskEntity;
import com.taxi.notificationservice.repository.NotificationTaskRepository;
import com.taxi.notificationservice.web.dto.NotificationTaskResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationTaskService {

	private final NotificationTaskRepository notificationTaskRepository;

	@Transactional
	public NotificationTaskResponse enqueue(
			Long tripId,
			RecipientType recipientType,
			Long recipientId,
			String message) {
		NotificationTaskEntity e = NotificationTaskEntity.builder()
				.tripId(tripId)
				.recipientType(recipientType)
				.recipientId(recipientId)
				.message(message)
				.status(NotificationTaskStatus.PENDING)
				.attempts(0)
				.createdAt(Instant.now())
				.build();
		notificationTaskRepository.save(e);
		return toResponse(e);
	}

	@Transactional(readOnly = true)
	public List<NotificationTaskResponse> listByTrip(Long tripId) {
		return notificationTaskRepository.findAllByTripIdOrderById(tripId).stream()
				.map(NotificationTaskService::toResponse)
				.toList();
	}

	private static NotificationTaskResponse toResponse(NotificationTaskEntity e) {
		return new NotificationTaskResponse(
				e.getId(),
				e.getTripId(),
				e.getRecipientType().name(),
				e.getRecipientId(),
				e.getMessage(),
				e.getStatus().name(),
				e.getAttempts(),
				e.getCreatedAt());
	}

}
