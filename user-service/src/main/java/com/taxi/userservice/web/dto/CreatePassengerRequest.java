package com.taxi.userservice.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreatePassengerRequest(
		@NotBlank String name,
		@NotBlank @Email String email,
		@NotBlank String phone) {
}
