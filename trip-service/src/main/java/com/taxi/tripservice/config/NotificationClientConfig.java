package com.taxi.tripservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class NotificationClientConfig {

	@Bean
	RestClient notificationRestClient(
			@Value("${taxi.notification-service.base-url:http://localhost:8083}") String baseUrl,
			@Value("${taxi.internal.api-key:dev-internal-key}") String apiKey) {
		return RestClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader("X-Internal-Api-Key", apiKey)
				.build();
	}
}
