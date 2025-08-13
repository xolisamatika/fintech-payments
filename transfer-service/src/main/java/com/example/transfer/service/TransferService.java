package com.example.transfer.service;

import com.example.common.dto.CreateTransferRequest;
import com.example.common.dto.TransferResponse;
import com.example.common.model.Transfer;
import com.example.transfer.client.LedgerClient;
import com.example.transfer.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository repo;
    private final LedgerClient ledger;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public TransferResponse createOrReplay(String idempotencyKey, CreateTransferRequest req, String requestId) {
        // fast path: existing response by Idempotency-Key
        var existing = repo.findByClientKey(idempotencyKey);
        if (existing.isPresent()) {
            var t = existing.get();
            return new TransferResponse(t.getTransferId(), t.getStatus(), t.getLastError());
        }

        // create NEW transfer row with server transferId
        var t = new Transfer();
        t.setClientKey(idempotencyKey);
        t.setTransferId(UUID.randomUUID().toString());
        t.setFromAccountId(req.fromAccountId());
        t.setToAccountId(req.toAccountId());
        t.setAmount(req.amount());
        t.setStatus("NEW");
        repo.saveAndFlush(t); // hold idempotency

        try {
            // single call to Ledger Service (atomic inside Ledger)
            ledger.transfer(requestId, t.getTransferId(), t.getFromAccountId(), t.getToAccountId(), t.getAmount()).block();
            t.setStatus("SUCCESS");
            t.setLastError(null);
        } catch (Exception ex) {
            t.setStatus("FAILED");
            t.setLastError(ex.getMessage());
            log.error("Ledger call failed transferId={}", t.getTransferId(), ex);
        }
        // persist final status
        return new TransferResponse(repo.save(t).getTransferId(), t.getStatus(), t.getLastError());
    }

    @Transactional()
    public TransferResponse get(String id) {
        var t = repo.findByTransferId(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new TransferResponse(t.getTransferId(), t.getStatus(), t.getLastError());
    }

    // batch: parallel processing
    public List<TransferResponse> processBatch(String requestId, List<CreateTransferRequest> batch, String idempotencyKeyPrefix) {
        if (batch.size() > 20) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "max 20");
        var exec = Executors.newFixedThreadPool(Math.min(8, batch.size()));
        try {
            return IntStream.range(0, batch.size()).mapToObj(i ->
                    CompletableFuture.supplyAsync(() ->
                            createOrReplay(idempotencyKeyPrefix + "-" + i, batch.get(i), requestId), exec)
            ).map(CompletableFuture::join).toList();
        } finally {
            exec.shutdown();
        }
    }
}

