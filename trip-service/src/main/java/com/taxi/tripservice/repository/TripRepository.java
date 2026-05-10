package com.taxi.tripservice.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taxi.tripservice.entity.TripEntity;

public interface TripRepository extends JpaRepository<TripEntity, Long> {

	List<TripEntity> findAllByPassengerIdOrderByCreatedAtDesc(Long passengerId);

	@Query("""
			SELECT COUNT(t), COALESCE(AVG(t.price), 0)
			FROM TripEntity t
			WHERE t.createdAt >= :start AND t.createdAt < :end
			""")
	Object[] countAndAvgPriceBetween(@Param("start") Instant start, @Param("end") Instant end);

	default TripStats statsForUtcDay(Instant dayStart, Instant dayEnd) {
		Object[] row = countAndAvgPriceBetween(dayStart, dayEnd);
		long count = row[0] == null ? 0L : ((Number) row[0]).longValue();
		BigDecimal avg = row[1] == null ? BigDecimal.ZERO : (BigDecimal) row[1];
		return new TripStats(count, avg);
	}

	record TripStats(long tripCount, BigDecimal averagePrice) {
	}
}
