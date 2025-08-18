package com.example.transfer.client;

import com.example.common.dto.TransferRequest;
import com.example.common.dto.TransferResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class LedgerClient {
    private final WebClient ledgerWebClient;

    @CircuitBreaker(name = "ledger", fallbackMethod = "fallback")
    public Mono<TransferResponse> transfer(String transferId, Long from, Long to, BigDecimal amount) {

        return ledgerWebClient.post()
                .uri("/ledger/transfer")
                .bodyValue(new TransferRequest(transferId, from, to, amount))
                .retrieve()
                .bodyToMono(TransferResponse.class)
                .doOnNext(r -> log.info("Got response from Ledger: {}", r));
    }

    private Mono<TransferResponse> fallback(String transferId, Long from, Long to, BigDecimal amount, Throwable ex) {
        return Mono.just(new TransferResponse(transferId, false, ex.getLocalizedMessage()));
    }
}