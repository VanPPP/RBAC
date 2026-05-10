package com.taxi.tripservice.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taxi.tripservice.repository.TripRepository.TripStats;
import com.taxi.tripservice.service.TripService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

	private final TripService tripService;

	@GetMapping("/trips")
	public TripStats tripStats(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return tripService.statsForDay(date);
	}
}
