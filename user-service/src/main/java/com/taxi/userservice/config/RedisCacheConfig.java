package com.taxi.userservice.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisCacheConfig {

	@Bean
	RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
		var json = RedisSerializationContext.SerializationPair.fromSerializer(
				new GenericJackson2JsonRedisSerializer());
		return builder -> builder
				.withCacheConfiguration(
						"availableDrivers",
						RedisCacheConfiguration.defaultCacheConfig()
								.entryTtl(Duration.ofMinutes(5))
								.serializeValuesWith(json));
	}
}
