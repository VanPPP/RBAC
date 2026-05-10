package com.taxi.tripservice.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.taxi.common.domain.DriverStatus;
import com.taxi.common.domain.TripStatus;
import com.taxi.common.dto.TripDto;
import com.taxi.tripservice.client.NotificationServiceClient;
import com.taxi.tripservice.client.UserServiceClient;
import com.taxi.tripservice.entity.TripEntity;
import com.taxi.tripservice.pricing.RoutePricing;
import com.taxi.tripservice.repository.TripRepository;
import com.taxi.tripservice.repository.TripRepository.TripStats;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

	private static final Set<TripStatus> TERMINAL = EnumSet.of(TripStatus.COMPLETED, TripStatus.CANCELLED);

	private final TripRepository tripRepository;
	private final UserServiceClient userServiceClient;
	private final RoutePricing routePricing;
	private final NotificationServiceClient notificationServiceClient;

	@Transactional
	public TripDto createTrip(Long passengerId, String origin, String destination) {
		if (userServiceClient.findPassenger(passengerId).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger not found");
		}
		var driver = userServiceClient.acquireDriver()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No available drivers"));

		double distanceKm = routePricing.estimateDistanceKm(origin, destination);
		var price = routePricing.priceForDistance(distanceKm);
		Instant now = Instant.now();
		TripEntity trip = TripEntity.builder()
				.passengerId(passengerId)
				.driverId(driver.getId())
				.status(TripStatus.ASSIGNED)
				.origin(origin)
				.destination(destination)
				.price(price)
				.distanceKm(distanceKm)
				.createdAt(now)
				.updatedAt(now)
				.build();
		tripRepository.save(trip);
		publishAndNotify(trip, "Trip assigned to driver");
		return toDto(trip);
	}

	@Transactional(readOnly = true)
	public TripDto getTrip(Long id) {
		return tripRepository.findById(id).map(TripService::toDto)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<TripDto> listForPassenger(Long passengerId) {
		return tripRepository.findAllByPassengerIdOrderByCreatedAtDesc(passengerId).stream()
				.map(TripService::toDto)
				.toList();
	}

	@Transactional
	public TripDto updateStatus(Long tripId, TripStatus newStatus) {
		TripEntity trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		validateTransition(trip.getStatus(), newStatus);
		trip.setStatus(newStatus);
		trip.setUpdatedAt(Instant.now());
		if (newStatus == TripStatus.COMPLETED || newStatus == TripStatus.CANCELLED) {
			releaseDriverIfNeeded(trip, newStatus);
		}
		publishAndNotify(trip, "Trip status: " + newStatus);
		return toDto(trip);
	}

	@Transactional
	public TripDto rateTrip(Long tripId, int rating) {
		if (rating < 1 || rating > 5) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be 1..5");
		}
		TripEntity trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (trip.getStatus() != TripStatus.COMPLETED) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Trip must be completed to rate");
		}
		trip.setRating(rating);
		trip.setUpdatedAt(Instant.now());
		return toDto(trip);
	}

	@Transactional(readOnly = true)
	public TripStats statsForDay(LocalDate day) {
		Instant start = day.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant end = day.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
		return tripRepository.statsForUtcDay(start, end);
	}

	private void validateTransition(TripStatus current, TripStatus next) {
		if (TERMINAL.contains(current)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Trip is finished");
		}
		boolean ok = switch (current) {
			case ASSIGNED -> next == TripStatus.ACCEPTED || next == TripStatus.CANCELLED;
			case ACCEPTED -> next == TripStatus.IN_PROGRESS || next == TripStatus.CANCELLED;
			case IN_PROGRESS -> next == TripStatus.COMPLETED || next == TripStatus.CANCELLED;
			default -> false;
		};
		if (!ok) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status transition " + current + " -> " + next);
		}
	}

	private void releaseDriverIfNeeded(TripEntity trip, TripStatus endStatus) {
		if (trip.getDriverId() == null) {
			return;
		}
		try {
			userServiceClient.updateDriverStatus(trip.getDriverId(), DriverStatus.AVAILABLE.name());
		} catch (Exception ex) {
			if (endStatus == TripStatus.COMPLETED) {
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "User service unavailable", ex);
			}
		}
	}

	private void publishAndNotify(TripEntity trip, String message) {
		notificationServiceClient.enqueueTripUpdate(
				trip.getId(),
				trip.getPassengerId(),
				trip.getDriverId(),
				message);
	}

	private static TripDto toDto(TripEntity t) {
		return TripDto.builder()
				.id(t.getId())
				.passengerId(t.getPassengerId())
				.driverId(t.getDriverId())
				.status(t.getStatus().name())
				.origin(t.getOrigin())
				.destination(t.getDestination())
				.price(t.getPrice())
				.rating(t.getRating())
				.distanceKm(t.getDistanceKm())
				.build();
	}
}
