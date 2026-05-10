package com.taxi.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.taxi.common.domain.DriverStatus;
import com.taxi.userservice.entity.DriverEntity;

public interface DriverRepository extends JpaRepository<DriverEntity, Long> {

	List<DriverEntity> findAllByStatusOrderById(DriverStatus status);

	@Query(value = """
			SELECT id FROM drivers
			WHERE status = 'AVAILABLE'
			ORDER BY id
			FOR UPDATE SKIP LOCKED
			LIMIT 1
			""", nativeQuery = true)
	List<Long> lockOneAvailableDriverId();
}
