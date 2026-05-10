package com.taxi.tripservice.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RateTripRequest(@NotNull @Min(1) @Max(5) Integer rating) {
}
