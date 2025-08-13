package com.example.ledger.controller;

import com.example.ledger.dto.TransferRequest;
import com.example.ledger.service.LedgerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LedgerControllerTest {

    @Mock
    private LedgerService ledgerService;

    @InjectMocks
    private LedgerController ledgerController;

    private TransferRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new TransferRequest("tx123", 1L, 2L, BigDecimal.valueOf(100));
    }

    @Test
    void transfer_success() {
        when(ledgerService.applyTransfer(request)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = ledgerController.transfer(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .containsEntry("transferId", "tx123")
                .containsEntry("success", true);

        verify(ledgerService).applyTransfer(request);
    }

    @Test
    void transfer_failure() {
        when(ledgerService.applyTransfer(request)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = ledgerController.transfer(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody())
                .containsEntry("transferId", "tx123")
                .containsEntry("success", false);

        verify(ledgerService).applyTransfer(request);
    }
}
