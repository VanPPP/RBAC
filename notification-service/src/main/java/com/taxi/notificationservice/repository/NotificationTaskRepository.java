package com.taxi.notificationservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taxi.notificationservice.entity.NotificationTaskEntity;

public interface NotificationTaskRepository extends JpaRepository<NotificationTaskEntity, Long> {

	List<NotificationTaskEntity> findAllByTripIdOrderById(Long tripId);
}
