package com.example.ledger.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {
    private final HealthEndpoint healthEndpoint;

    @GetMapping("/health")
    public HealthResponse getHealth() {
        var health = healthEndpoint.health();
        return new HealthResponse(health.getStatus().getCode());
    }

    public record HealthResponse(String status) {}
}
