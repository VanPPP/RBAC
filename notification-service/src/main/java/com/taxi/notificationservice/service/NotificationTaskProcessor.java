package com.taxi.notificationservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.taxi.common.domain.NotificationTaskStatus;
import com.taxi.notificationservice.entity.NotificationTaskEntity;
import com.taxi.notificationservice.repository.NotificationTaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTaskProcessor {

	private final NotificationTaskRepository notificationTaskRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void process(Long taskId) {
		NotificationTaskEntity task = notificationTaskRepository.findById(taskId).orElse(null);
		if (task == null || task.getStatus() != NotificationTaskStatus.PROCESSING) {
			return;
		}
		try {
			sendNotification(task);
			task.setStatus(NotificationTaskStatus.SENT);
		} catch (Exception ex) {
			log.warn("Notification failed id={}: {}", taskId, ex.getMessage());
			task.setAttempts(task.getAttempts() + 1);
			if (task.getAttempts() >= 3) {
				task.setStatus(NotificationTaskStatus.FAILED);
			} else {
				task.setStatus(NotificationTaskStatus.PENDING);
			}
		}
	}

	private static void sendNotification(NotificationTaskEntity task) throws InterruptedException {
		log.info("[notify] type={} recipientId={} msg={}",
				task.getRecipientType(), task.getRecipientId(), task.getMessage());
		Thread.sleep(400);
	}
}
