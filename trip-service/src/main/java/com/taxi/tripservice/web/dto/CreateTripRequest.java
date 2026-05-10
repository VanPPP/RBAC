package com.taxi.tripservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTripRequest(
		@NotNull Long passengerId,
		@NotBlank String origin,
		@NotBlank String destination) {
}
