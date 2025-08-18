package com.example.ledger.controller;

import com.example.common.dto.AccountResponse;
import com.example.common.dto.CreateAccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountsControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAccount_andGet() {
        CreateAccountRequest req = new CreateAccountRequest(BigDecimal.valueOf(250));

        ResponseEntity<AccountResponse> createResp = restTemplate.postForEntity(
                "http://localhost:" + port + "/accounts",
                req,
                AccountResponse.class
        );

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        AccountResponse account = createResp.getBody();
        assertThat(account).isNotNull();
        assertThat(account.balance()).isEqualByComparingTo(BigDecimal.valueOf(250));

        ResponseEntity<AccountResponse> getResp = restTemplate.getForEntity(
                "http://localhost:" + port + "/accounts/" + account.id(),
                AccountResponse.class
        );

        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody()).isNotNull();
        assertThat(getResp.getBody().id()).isEqualTo(account.id());
        assertThat(getResp.getBody().balance()).isEqualByComparingTo(BigDecimal.valueOf(250));
    }

    @Test
    void getAccount_notFound() {
        ResponseEntity<AccountResponse> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/accounts/9999",
                AccountResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
