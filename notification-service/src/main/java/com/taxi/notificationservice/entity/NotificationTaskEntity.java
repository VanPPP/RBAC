package com.taxi.notificationservice.entity;

import java.time.Instant;

import com.taxi.common.domain.NotificationTaskStatus;
import com.taxi.common.domain.RecipientType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_tasks", indexes = @Index(name = "idx_notification_tasks_status", columnList = "status"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTaskEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "trip_id", nullable = false)
	private Long tripId;

	@Enumerated(EnumType.STRING)
	@Column(name = "recipient_type", nullable = false)
	private RecipientType recipientType;

	@Column(name = "recipient_id", nullable = false)
	private Long recipientId;

	@Column(nullable = false, length = 2000)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationTaskStatus status;

	@Column(nullable = false)
	private int attempts;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
}
