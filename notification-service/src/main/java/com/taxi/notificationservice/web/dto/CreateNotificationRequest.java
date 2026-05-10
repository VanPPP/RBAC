package com.taxi.notificationservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNotificationRequest(
		@NotNull Long tripId,
		@NotNull String recipientType,
		@NotNull Long recipientId,
		@NotBlank String message) {
}
