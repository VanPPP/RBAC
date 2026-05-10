package com.taxi.tripservice.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationServiceClient {

	private final RestClient notificationRestClient;

	public NotificationServiceClient(@Qualifier("notificationRestClient") RestClient notificationRestClient) {
		this.notificationRestClient = notificationRestClient;
	}

	public void enqueueTripUpdate(Long tripId, Long passengerId, Long driverId, String baseMessage) {
		String base = baseMessage != null ? baseMessage : "Trip update";
		if (passengerId != null) {
			postOne(tripId, "PASSENGER", passengerId, base + " (passenger)");
		}
		if (driverId != null) {
			postOne(tripId, "DRIVER", driverId, base + " (driver)");
		}
	}

	private void postOne(Long tripId, String recipientType, Long recipientId, String message) {
		try {
			notificationRestClient.post()
					.uri("/notifications")
					.body(new CreateNotificationBody(tripId, recipientType, recipientId, message))
					.retrieve()
					.toBodilessEntity();
		} catch (Exception ex) {
			log.warn("Notification service unavailable: {}", ex.getMessage());
		}
	}

	private record CreateNotificationBody(Long tripId, String recipientType, Long recipientId, String message) {
	}
}
