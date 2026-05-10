package com.taxi.notificationservice.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.taxi.notificationservice.service.NotificationTaskClaimService;
import com.taxi.notificationservice.service.NotificationTaskProcessor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWorkerPool {

	private final NotificationTaskClaimService claimService;
	private final NotificationTaskProcessor processor;

	@Value("${taxi.notification.worker-threads:4}")
	private int workerThreads;

	private ExecutorService executor;

	@PostConstruct
	void start() {
		executor = Executors.newFixedThreadPool(workerThreads);
		for (int i = 0; i < workerThreads; i++) {
			executor.submit(this::workerLoop);
		}
		log.info("Notification worker pool started ({} threads)", workerThreads);
	}

	private void workerLoop() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				var claimed = claimService.claimNextPendingId();
				if (claimed.isEmpty()) {
					Thread.sleep(300);
					continue;
				}
				processor.process(claimed.get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			} catch (Exception e) {
				log.warn("Worker iteration failed: {}", e.getMessage());
			}
		}
	}

	@PreDestroy
	void stop() throws InterruptedException {
		if (executor == null) {
			return;
		}
		executor.shutdownNow();
		boolean finished = executor.awaitTermination(30, TimeUnit.SECONDS);
		log.info("Notification worker pool stopped (terminated in time: {})", finished);
	}
}
