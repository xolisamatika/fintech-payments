package com.example.ledger.controller;

import com.example.common.dto.TransferRequest;
import com.example.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService service;

    @PostMapping("/transfer")
    public ResponseEntity<Map<String,Object>> transfer(@RequestBody TransferRequest req) {
        boolean ok = service.applyTransfer(req);
        return ResponseEntity.status(ok ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("transferId", req.transferId(), "success", ok));
    }
}
