package com.example.transfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient ledgerWebClient(
            @Value("${ledger.service.url}") String baseUrl,
            @Value("${ledger.service.timeout-ms}") long timeoutMs
    ){
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(timeoutMs));
        return WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(ExchangeStrategies.builder().build())
                .build()
                .mutate()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}