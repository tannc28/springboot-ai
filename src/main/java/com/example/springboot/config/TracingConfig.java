package com.example.springboot.config;

import io.micrometer.tracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.MDC;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
public class TracingConfig {

    @Bean
    public Filter traceIdFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                try {
                    if (request instanceof HttpServletRequest) {
                        String traceId = ((HttpServletRequest) request).getHeader("X-Trace-Id");
                        if (traceId != null) {
                            MDC.put("traceId", traceId);
                        }
                    }
                    chain.doFilter(request, response);
                } finally {
                    MDC.clear();
                }
            }
        };
    }
} 