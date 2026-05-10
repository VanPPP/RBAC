package com.taxi.tripservice.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.common.domain.TripStatus;
import com.taxi.common.dto.TripDto;
import com.taxi.tripservice.repository.TripRepository.TripStats;
import com.taxi.tripservice.service.TripService;
import com.taxi.tripservice.web.dto.CreateTripRequest;
import com.taxi.tripservice.web.dto.RateTripRequest;
import com.taxi.tripservice.web.dto.UpdateTripStatusRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

	private final TripService tripService;

	@GetMapping("/stats")
	public TripStats tripStatsForDay(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return tripService.statsForDay(date);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TripDto create(@Valid @RequestBody CreateTripRequest body) {
		return tripService.createTrip(body.passengerId(), body.origin(), body.destination());
	}

	@GetMapping("/{id:\\d+}")
	public TripDto get(@PathVariable Long id) {
		return tripService.getTrip(id);
	}

	@GetMapping
	public List<TripDto> list(@RequestParam("passenger_id") Long passengerId) {
		return tripService.listForPassenger(passengerId);
	}

	@PatchMapping("/{id:\\d+}/status")
	public TripDto patchStatus(@PathVariable Long id, @Valid @RequestBody UpdateTripStatusRequest body) {
		TripStatus status = TripStatus.valueOf(body.status());
		return tripService.updateStatus(id, status);
	}

	@PatchMapping("/{id:\\d+}/rating")
	public TripDto rate(@PathVariable Long id, @Valid @RequestBody RateTripRequest body) {
		return tripService.rateTrip(id, body.rating());
	}
}
