package com.academic.erp.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String health() {
        return "Backend Working ✔️";
    }

    @GetMapping("/api/test")
    public Map<String, Object> test() {
        return Map.of(
                "status", "UP",
                "message", "Spring Boot Backend is Running",
                "timestamp", LocalDateTime.now().toString()
        );
    }

}
