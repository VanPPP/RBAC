package com.taxi.userservice.web.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank String subject, @NotBlank String role) {
}
