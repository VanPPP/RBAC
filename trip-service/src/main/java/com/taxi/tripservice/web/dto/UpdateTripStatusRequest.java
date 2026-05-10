package com.taxi.tripservice.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTripStatusRequest(@NotBlank String status) {
}
