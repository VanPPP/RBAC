package com.taxi.userservice.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateDriverRequest(
		@NotBlank String name,
		@NotBlank @Email String email,
		@NotBlank String phone,
		@NotBlank String licenseNumber) {
}
