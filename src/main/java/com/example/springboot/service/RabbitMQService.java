package com.example.springboot.service;

import com.example.springboot.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    // Product Messages
    public void sendProductMessage(String routingKey, Object message) {
        log.info("Sending product message with routing key: {}", routingKey);
        rabbitTemplate.convertAndSend(RabbitMQConfig.PRODUCT_EXCHANGE, routingKey, message);
    }

    @RabbitListener(queues = RabbitMQConfig.PRODUCT_QUEUE)
    public void receiveProductMessage(Object message) {
        log.info("Received product message: {}", message);
        // Process product messages
    }

    // User Messages
    public void sendUserMessage(String routingKey, Object message) {
        log.info("Sending user message with routing key: {}", routingKey);
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EXCHANGE, routingKey, message);
    }

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void receiveUserMessage(Object message) {
        log.info("Received user message: {}", message);
        // Process user messages
    }

    // Order Messages
    public void sendOrderMessage(String routingKey, Object message) {
        log.info("Sending order message with routing key: {}", routingKey);
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, routingKey, message);
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void receiveOrderMessage(Object message) {
        log.info("Received order message: {}", message);
        // Process order messages
    }

    // Audit Messages
    public void sendAuditMessage(String routingKey, Object message) {
        log.info("Sending audit message with routing key: {}", routingKey);
        rabbitTemplate.convertAndSend(RabbitMQConfig.AUDIT_EXCHANGE, routingKey, message);
    }

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void receiveAuditMessage(Object message) {
        log.info("Received audit message: {}", message);
        // Process audit messages
    }

    // Notification Messages
    public void sendNotificationMessage(String routingKey, Object message) {
        log.info("Sending notification message with routing key: {}", routingKey);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, routingKey, message);
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void receiveNotificationMessage(Object message) {
        log.info("Received notification message: {}", message);
        // Process notification messages
    }

    // Generic method for sending to any exchange
    public void sendToExchange(String exchange, String routingKey, Object message) {
        log.info("Sending message to exchange: {} with routing key: {}", exchange, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    // Dead letter queue handler
    @RabbitListener(queues = "dead.letter.queue")
    public void handleDeadLetterMessage(Object message) {
        log.warn("Processing dead letter message: {}", message);
        // Handle failed messages
    }

    // Error handling
    public void handleRabbitMQError(Throwable throwable) {
        log.error("RabbitMQ error occurred: {}", throwable.getMessage(), throwable);
    }
} 