package com.taxi.notificationservice.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.common.domain.RecipientType;
import com.taxi.notificationservice.service.NotificationTaskService;
import com.taxi.notificationservice.web.dto.CreateNotificationRequest;
import com.taxi.notificationservice.web.dto.NotificationTaskResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationTaskService notificationTaskService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public NotificationTaskResponse create(@Valid @RequestBody CreateNotificationRequest body) {
		RecipientType type = RecipientType.valueOf(body.recipientType());
		return notificationTaskService.enqueue(body.tripId(), type, body.recipientId(), body.message());
	}

	@GetMapping
	public List<NotificationTaskResponse> list(@RequestParam("trip_id") Long tripId) {
		return notificationTaskService.listByTrip(tripId);
	}
}
