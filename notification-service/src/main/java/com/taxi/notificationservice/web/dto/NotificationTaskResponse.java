package com.taxi.notificationservice.web.dto;

import java.time.Instant;

public record NotificationTaskResponse(
		Long id,
		Long tripId,
		String recipientType,
		Long recipientId,
		String message,
		String status,
		int attempts,
		Instant createdAt) {
}
