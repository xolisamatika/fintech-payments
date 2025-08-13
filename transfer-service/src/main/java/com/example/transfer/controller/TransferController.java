package com.example.transfer.controller;

import com.example.common.dto.CreateTransferRequest;
import com.example.common.dto.TransferResponse;
import com.example.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;
    @PostMapping
    public ResponseEntity<TransferResponse> create(
            @RequestHeader("Idempotency-Key") String key,
            @RequestHeader(value = "X-Request-Id", required = false) String reqId,
            @Valid @RequestBody CreateTransferRequest body) {
        var rid = Optional.ofNullable(reqId).orElse(UUID.randomUUID().toString());
        var resp = service.createOrReplay(key, body, rid);
        return ResponseEntity.status("SUCCESS".equals(resp.status()) ? 200 : 422).body(resp);
    }

    @GetMapping("/{id}")
    public TransferResponse get(@PathVariable String id) { return service.get(id); }

    @PostMapping("/batch")
    public List<TransferResponse> batch(
            @RequestHeader("Idempotency-Key") String keyPrefix,
            @RequestHeader(value = "X-Request-Id", required = false) String reqId,
            @RequestBody List<@Valid CreateTransferRequest> items) {
        var rid = Optional.ofNullable(reqId).orElse(UUID.randomUUID().toString());
        return service.processBatch(rid, items, keyPrefix);
    }
}

