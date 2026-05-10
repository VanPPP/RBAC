package com.taxi.notificationservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationTaskClaimService {

	private static final String CLAIM_SQL = """
			WITH next_task AS (
				SELECT id FROM notification_tasks
				WHERE status = 'PENDING' AND attempts < 3
				ORDER BY id
				FOR UPDATE SKIP LOCKED
				LIMIT 1
			)
			UPDATE notification_tasks n
			SET status = 'PROCESSING'
			FROM next_task t
			WHERE n.id = t.id
			RETURNING n.id
			""";

	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public Optional<Long> claimNextPendingId() {
		List<Long> ids = jdbcTemplate.query(CLAIM_SQL, (rs, rowNum) -> rs.getLong(1));
		return ids.stream().findFirst();
	}
}
