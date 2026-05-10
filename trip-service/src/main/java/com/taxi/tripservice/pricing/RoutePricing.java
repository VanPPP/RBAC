package com.taxi.tripservice.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RoutePricing {

	private final BigDecimal tariffPerKm;

	public RoutePricing(@Value("${taxi.pricing.tariff-per-km:50}") BigDecimal tariffPerKm) {
		this.tariffPerKm = tariffPerKm;
	}

	public double estimateDistanceKm(String origin, String destination) {
		int h = Objects.hash(origin, destination);
		return 1 + (Math.abs(h) % 50);
	}

	public BigDecimal priceForDistance(double distanceKm) {
		return tariffPerKm.multiply(BigDecimal.valueOf(distanceKm)).setScale(2, RoundingMode.HALF_UP);
	}
}
