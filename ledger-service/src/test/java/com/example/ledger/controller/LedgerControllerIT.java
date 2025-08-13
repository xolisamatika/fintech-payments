package com.example.ledger.controller;

import com.example.ledger.dto.TransferRequest;
import com.example.ledger.service.LedgerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LedgerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LedgerService ledgerService;

    @Test
    void transfer_returns200_whenSuccess() throws Exception {
        when(ledgerService.applyTransfer(any())).thenReturn(true);

        TransferRequest req = new TransferRequest("tx123", 1L, 2L, BigDecimal.valueOf(100));

        mockMvc.perform(post("/ledger/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value("tx123"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void transfer_returns422_whenFailure() throws Exception {
        when(ledgerService.applyTransfer(any())).thenReturn(false);

        TransferRequest req = new TransferRequest("tx124", 1L, 2L, BigDecimal.valueOf(50));

        mockMvc.perform(post("/ledger/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.transferId").value("tx124"))
                .andExpect(jsonPath("$.success").value(false));
    }
}
