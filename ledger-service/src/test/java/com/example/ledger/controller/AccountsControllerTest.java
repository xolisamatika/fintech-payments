package com.example.ledger.controller;

import com.example.common.model.Account;
import com.example.ledger.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_success() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(100));
        account.setVersion(0L);

        Mockito.when(accountService.createAccount(any(Account.class))).thenReturn(account);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "initialBalance": 100
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(100))
                .andExpect(jsonPath("$.version").value(0));
    }

    @Test
    void createAccount_validationFail() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAccount_found() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(500));
        account.setVersion(2L);

        Mockito.when(accountService.fetchAccountById(1L)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.balance").value(500))
                .andExpect(jsonPath("$.version").value(2));
    }

    @Test
    void getAccount_notFound() throws Exception {
        Mockito.when(accountService.fetchAccountById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/accounts/99"))
                .andExpect(status().isNotFound());
    }
}
