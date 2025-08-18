package com.example.transfer.config;

import com.example.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.CompletableFuture;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(value = "expired-transfers-scheduler.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ExpiredTransfersSchedulerConfig {

    private final TransferService service;

    @Scheduled(cron = "${expired-transfers-scheduler.cron}")
    public void scheduleExpiredTaskUpdates() {
        log.info("Scheduling removal of expired transfers...");
        CompletableFuture<Integer> integerCompletableFuture = service.cleanUpExpiredTransfers();
        log.info("Idempotency cleanup removed {} expired records", integerCompletableFuture);
    }
}
