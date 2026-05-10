package com.taxi.common.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDto {

	private Long id;
	private Long passengerId;
	private Long driverId;
	private String status;
	private String origin;
	private String destination;
	private BigDecimal price;
	private Integer rating;
	private Double distanceKm;
}
