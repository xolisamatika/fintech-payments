package com.example.ledger.controller;

import com.example.ledger.dto.AccountResponse;
import com.example.ledger.dto.CreateAccountRequest;
import com.example.ledger.model.Account;
import com.example.ledger.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountService service;

    @PostMapping()
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest req) {
        Account account = new Account();
        account.setBalance(req.initialBalance() == null ? BigDecimal.ZERO : req.initialBalance());
        account = service.createAccount(account);
        return new AccountResponse(account.getId(), account.getBalance(), account.getVersion());
    }

    @GetMapping("/accounts/{id}")
    public AccountResponse get(@PathVariable Long id) {
        Account a = service.fetchAccountById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new AccountResponse(a.getId(), a.getBalance(), a.getVersion());
    }
}