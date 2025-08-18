package com.example.ledger.controller;

import com.example.ledger.service.LedgerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LedgerController.class)
class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LedgerService ledgerService;

    @Test
    void transfer_success() throws Exception {
        Mockito.when(ledgerService.transfer(anyString(), anyLong(), anyLong(), any(BigDecimal.class)))
                .thenReturn(true);

        mockMvc.perform(post("/ledger/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "transferId": "t1",
                                    "fromAccountId": 1,
                                    "toAccountId": 2,
                                    "amount": 100
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value("t1"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void transfer_failure() throws Exception {
        Mockito.when(ledgerService.transfer(anyString(), anyLong(), anyLong(), any(BigDecimal.class)))
                .thenReturn(false);

        mockMvc.perform(post("/ledger/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "transferId": "t2",
                                    "fromAccountId": 1,
                                    "toAccountId": 2,
                                    "amount": 200
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.transferId").value("t2"))
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    void transfer_exception() throws Exception {
        Mockito.when(ledgerService.transfer(anyString(), anyLong(), anyLong(), any(BigDecimal.class)))
                .thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(post("/ledger/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "transferId": "t3",
                                    "fromAccountId": 1,
                                    "toAccountId": 2,
                                    "amount": 300
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.transferId").value("t3"))
                .andExpect(jsonPath("$.status").value(false));
    }
}
