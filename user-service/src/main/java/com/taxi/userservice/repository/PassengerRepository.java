package com.taxi.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taxi.userservice.entity.PassengerEntity;

public interface PassengerRepository extends JpaRepository<PassengerEntity, Long> {
}
