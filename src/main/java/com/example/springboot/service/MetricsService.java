package com.example.springboot.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;

    private Counter productCreatedCounter;
    private Counter productUpdatedCounter;
    private Counter productDeletedCounter;
    private Counter userRegisteredCounter;
    private Counter userLoginCounter;
    private Timer productOperationTimer;

    public void incrementProductCreated() {
        if (productCreatedCounter == null) {
            productCreatedCounter = Counter.builder("product.created")
                    .description("Number of products created")
                    .register(meterRegistry);
        }
        productCreatedCounter.increment();
    }

    public void incrementProductUpdated() {
        if (productUpdatedCounter == null) {
            productUpdatedCounter = Counter.builder("product.updated")
                    .description("Number of products updated")
                    .register(meterRegistry);
        }
        productUpdatedCounter.increment();
    }

    public void incrementProductDeleted() {
        if (productDeletedCounter == null) {
            productDeletedCounter = Counter.builder("product.deleted")
                    .description("Number of products deleted")
                    .register(meterRegistry);
        }
        productDeletedCounter.increment();
    }

    public void incrementUserRegistered() {
        if (userRegisteredCounter == null) {
            userRegisteredCounter = Counter.builder("user.registered")
                    .description("Number of users registered")
                    .register(meterRegistry);
        }
        userRegisteredCounter.increment();
    }

    public void incrementUserLogin() {
        if (userLoginCounter == null) {
            userLoginCounter = Counter.builder("user.login")
                    .description("Number of user logins")
                    .register(meterRegistry);
        }
        userLoginCounter.increment();
    }

    public Timer.Sample startProductOperationTimer() {
        if (productOperationTimer == null) {
            productOperationTimer = Timer.builder("product.operation.duration")
                    .description("Time taken for product operations")
                    .register(meterRegistry);
        }
        return Timer.start(meterRegistry);
    }

    public void stopProductOperationTimer(Timer.Sample sample) {
        if (sample != null) {
            sample.stop(productOperationTimer);
        }
    }

    public void recordProductOperationTime(long timeInMs) {
        if (productOperationTimer == null) {
            productOperationTimer = Timer.builder("product.operation.duration")
                    .description("Time taken for product operations")
                    .register(meterRegistry);
        }
        productOperationTimer.record(timeInMs, TimeUnit.MILLISECONDS);
    }
} 