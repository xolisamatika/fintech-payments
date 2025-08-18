package com.example.transfer.service;

import com.example.common.dto.CreateTransferRequest;
import com.example.common.dto.TransferResponse;
import com.example.common.model.Transfer;
import com.example.transfer.client.LedgerClient;
import com.example.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.example.transfer.config.AsyncConfiguration.JOB_ASYNC_EXECUTOR;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final LedgerClient ledger;

    @Value("${expired-transfers-scheduler.ttl-hours}")
    private int ttlHours;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transfer get(String id) {
        return transferRepository.findByTransferId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @Transactional
    public Transfer transfer(String idempotencyKey, Long from, Long to, BigDecimal amount) {
        return transferRepository.findByIdempotencyKey(idempotencyKey).orElseGet(() -> {
            String transferId = UUID.randomUUID().toString();
            Transfer t = new Transfer(null, idempotencyKey, "PENDING", transferId, Instant.now());
            transferRepository.save(t);

            try {
                TransferResponse transferResponse = ledger.transfer(t.getTransferId(), from, to, amount).block();
                log.info("Response {}", transferResponse);
                assert transferResponse != null;
                t.setStatus(transferResponse.status() ? "SUCCESS" : "FAILURE");
            } catch (Exception e) {
                t.setStatus("FAILURE");
            }
            return transferRepository.save(t);
        });
    }

    @Async(JOB_ASYNC_EXECUTOR)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Integer> cleanUpExpiredTransfers() {
        log.info("Closing expired transfers");
        int deleteExpired = transferRepository.deleteExpired(Instant.now().minus(Duration.ofHours(ttlHours)));

        return CompletableFuture.completedFuture(deleteExpired);
    }

    @Async(JOB_ASYNC_EXECUTOR)
    public CompletableFuture<List<Transfer>> batchTransfers(String idempotencyKeyPrefix, List<CreateTransferRequest> batch) {
        List<CompletableFuture<Transfer>> futures = batch.stream()
                .map(r -> CompletableFuture.supplyAsync(() ->
                        transfer(idempotencyKeyPrefix, r.fromAccountId(), r.toAccountId(), r.amount())))
                .toList();
        return CompletableFuture.completedFuture(futures.stream().map(CompletableFuture::join).toList());
    }
}

