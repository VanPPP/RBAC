package com.taxi.userservice.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDriverStatusRequest(@NotBlank String status) {
}
