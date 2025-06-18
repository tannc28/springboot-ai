package com.example.springboot.service;

import com.example.springboot.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Product Events
    public CompletableFuture<SendResult<String, Object>> publishProductEvent(String eventType, Object payload) {
        String topic = "product-events";
        String key = eventType;
        log.info("Publishing product event: {} to topic: {}", eventType, topic);
        return kafkaTemplate.send(topic, key, payload);
    }

    @KafkaListener(topics = "product-events", groupId = "product-service")
    public void consumeProductEvent(String message) {
        log.info("Received product event: {}", message);
        // Process product events
    }

    // User Events
    public CompletableFuture<SendResult<String, Object>> publishUserEvent(String eventType, Object payload) {
        String topic = "user-events";
        String key = eventType;
        log.info("Publishing user event: {} to topic: {}", eventType, topic);
        return kafkaTemplate.send(topic, key, payload);
    }

    @KafkaListener(topics = "user-events", groupId = "user-service")
    public void consumeUserEvent(String message) {
        log.info("Received user event: {}", message);
        // Process user events
    }

    // Order Events
    public CompletableFuture<SendResult<String, Object>> publishOrderEvent(String eventType, Object payload) {
        String topic = "order-events";
        String key = eventType;
        log.info("Publishing order event: {} to topic: {}", eventType, topic);
        return kafkaTemplate.send(topic, key, payload);
    }

    @KafkaListener(topics = "order-events", groupId = "order-service")
    public void consumeOrderEvent(String message) {
        log.info("Received order event: {}", message);
        // Process order events
    }

    // Audit Logs
    public CompletableFuture<SendResult<String, Object>> publishAuditLog(Object auditLog) {
        String topic = "audit-logs";
        log.info("Publishing audit log to topic: {}", topic);
        return kafkaTemplate.send(topic, auditLog);
    }

    @KafkaListener(topics = "audit-logs", groupId = "audit-service")
    public void consumeAuditLog(String message) {
        log.info("Received audit log: {}", message);
        // Process audit logs
    }

    // Generic method for publishing to any topic
    public CompletableFuture<SendResult<String, Object>> publishToTopic(String topic, String key, Object payload) {
        log.info("Publishing to topic: {} with key: {}", topic, key);
        return kafkaTemplate.send(topic, key, payload);
    }

    // Error handling
    public void handleKafkaError(Throwable throwable) {
        log.error("Kafka error occurred: {}", throwable.getMessage(), throwable);
    }
} 