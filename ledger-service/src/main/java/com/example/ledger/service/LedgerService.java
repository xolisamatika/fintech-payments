package com.example.ledger.service;

import com.example.common.model.Account;
import com.example.common.model.LedgerEntry;
import com.example.ledger.exception.AccountNotFoundException;
import com.example.ledger.exception.InsufficientFundsException;
import com.example.ledger.repository.AccountRepository;
import com.example.ledger.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public boolean transfer(String transferId, Long fromId, Long toId, BigDecimal amount) {
        if (ledgerEntryRepository.existsByTransferId(transferId)) return true;

        Account from = accountRepository.findById(fromId)
                .orElseThrow(() -> new AccountNotFoundException("From account not found"));
        Account to = accountRepository.findById(toId)
                .orElseThrow(() -> new AccountNotFoundException("To account not found"));

        if (from.getBalance().compareTo(amount) < 0)
            throw new InsufficientFundsException("Insufficient balance");

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.saveAll(List.of(from, to));

        ledgerEntryRepository.saveAll(List.of(
                new LedgerEntry(null, transferId, fromId, amount, LedgerEntry.EntryType.DEBIT, LocalDateTime.now()),
                new LedgerEntry(null, transferId, toId, amount, LedgerEntry.EntryType.CREDIT, LocalDateTime.now())
        ));
        return true;
    }
}
