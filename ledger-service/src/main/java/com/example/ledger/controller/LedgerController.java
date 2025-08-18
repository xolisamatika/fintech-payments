package com.example.ledger.controller;

import com.example.common.dto.TransferRequest;
import com.example.common.dto.TransferResponse;
import com.example.ledger.service.LedgerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService service;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest req) {
        log.info("LedgerController invoked with {}", req);
        try {
            boolean ok = service.transfer(req.transferId(), req.fromAccountId(), req.toAccountId(), req.amount());
            return ResponseEntity.status(ok ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new TransferResponse(req.transferId(), ok, null));
        } catch (Exception e) {
            log.error("LedgerController failed with exception {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new TransferResponse(req.transferId(), false, null));
        }
    }
}
