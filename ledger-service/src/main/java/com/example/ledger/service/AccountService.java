package com.example.ledger.service;

import com.example.ledger.model.Account;
import com.example.ledger.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> fetchAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }
}
