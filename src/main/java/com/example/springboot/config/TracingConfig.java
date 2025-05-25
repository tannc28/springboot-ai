package com.example.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.tracing.Tracer;
import org.slf4j.MDC;
import jakarta.servlet.Filter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class TracingConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Filter traceIdFilter(Tracer tracer) {
        return (request, response, chain) -> {
            String traceId = tracer.currentSpan().context().traceId();
            MDC.put("traceId", traceId);
            try {
                chain.doFilter(request, response);
            } finally {
                MDC.remove("traceId");
            }
        };
    }
} 