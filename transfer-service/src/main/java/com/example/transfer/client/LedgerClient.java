package com.example.transfer.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class LedgerClient {
    private final WebClient client;

    public LedgerClient(WebClient.Builder builder, @Value("${ledger.service.url}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name="ledger", fallbackMethod = "fallback")
    public Mono<Map> transfer(String requestId, String transferId, Long from, Long to, BigDecimal amount) {
        return client.post().uri("/ledger/transfer")
                .header("X-Request-Id", requestId)
                .bodyValue(Map.of("transferId", transferId, "fromAccountId", from, "toAccountId", to, "amount", amount))
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        rsp -> rsp.bodyToMono(String.class).map(msg -> new RuntimeException("Ledger error: " + msg)))
                .bodyToMono(Map.class);
    }

    private Mono<Map> fallback(String requestId, String transferId, Long from, Long to, BigDecimal amount, Throwable ex) {
        return Mono.error(new IllegalStateException("Ledger circuit/failure", ex));
    }
}
