package com.example.springboot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckConfig {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    @Component
    public class DatabaseHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(1000)) {
                    return Health.up()
                            .withDetail("database", "PostgreSQL")
                            .withDetail("status", "Connected")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("database", "PostgreSQL")
                            .withDetail("status", "Connection invalid")
                            .build();
                }
            } catch (Exception e) {
                log.error("Database health check failed", e);
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        }
    }

    @Component
    public class RedisHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            try {
                String result = redisTemplate.getConnectionFactory()
                        .getConnection()
                        .ping();
                
                if ("PONG".equals(result)) {
                    return Health.up()
                            .withDetail("cache", "Redis")
                            .withDetail("status", "Connected")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("cache", "Redis")
                            .withDetail("status", "Ping failed")
                            .build();
                }
            } catch (Exception e) {
                log.error("Redis health check failed", e);
                return Health.down()
                        .withDetail("cache", "Redis")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        }
    }
} 