package com.example.transfer.controller;

import com.example.common.dto.CreateTransferRequest;
import com.example.common.model.Transfer;
import com.example.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;
    @PostMapping
    public ResponseEntity<Transfer> create(
            @RequestHeader("Idempotency-Key") String idempotencyKeyPrefix,
            @Valid @RequestBody CreateTransferRequest request) {
        return ResponseEntity.ok(service.transfer(idempotencyKeyPrefix, request.fromAccountId(), request.toAccountId(), request.amount()));
    }

    @GetMapping("/{id}")
    public Transfer get(@PathVariable String id) { return service.get(id); }

    @PostMapping("/batch")
    public ResponseEntity<CompletableFuture<List<Transfer>>> batch(
            @RequestHeader("Idempotency-Key") String idempotencyKeyPrefix,
            @RequestBody List<@Valid CreateTransferRequest> items) {
        return ResponseEntity.ok(service.batchTransfers(idempotencyKeyPrefix, items));
    }
}

