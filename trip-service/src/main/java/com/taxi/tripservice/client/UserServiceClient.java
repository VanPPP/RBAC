package com.taxi.tripservice.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.taxi.common.dto.DriverDto;
import com.taxi.common.dto.PassengerDto;

@Component
public class UserServiceClient {

	private final RestClient userRestClient;

	public UserServiceClient(@Qualifier("userRestClient") RestClient userRestClient) {
		this.userRestClient = userRestClient;
	}

	public Optional<PassengerDto> findPassenger(Long id) {
		try {
			PassengerDto p = userRestClient.get()
					.uri("/internal/passengers/{id}", id)
					.retrieve()
					.body(PassengerDto.class);
			return Optional.ofNullable(p);
		} catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
			return Optional.empty();
		}
	}

	public Optional<DriverDto> acquireDriver() {
		try {
			DriverDto d = userRestClient.post()
					.uri("/internal/drivers/acquire")
					.retrieve()
					.body(DriverDto.class);
			return Optional.ofNullable(d);
		} catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
			return Optional.empty();
		}
	}

	public void updateDriverStatus(Long driverId, String status) {
		userRestClient.patch()
				.uri("/internal/drivers/{id}/status", driverId)
				.body(new StatusBody(status))
				.retrieve()
				.toBodilessEntity();
	}

	private record StatusBody(String status) {
	}
}
