package com.example.ledger.service;

import com.example.common.model.Account;
import com.example.ledger.exception.AccountNotFoundException;
import com.example.ledger.exception.InsufficientFundsException;
import com.example.ledger.repository.AccountRepository;
import com.example.ledger.repository.LedgerEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LedgerServiceTest {

    private AccountRepository accountRepository;
    private LedgerEntryRepository ledgerEntryRepository;
    private LedgerService ledgerService;

    @BeforeEach
    void setup() {
        accountRepository = Mockito.mock(AccountRepository.class);
        ledgerEntryRepository = Mockito.mock(LedgerEntryRepository.class);
        ledgerService = new LedgerService(accountRepository, ledgerEntryRepository);
    }

    @Test
    void transfer_successful() {
        Account from = new Account();
        from.setId(1L);
        from.setBalance(BigDecimal.valueOf(200));

        Account to = new Account();
        to.setId(2L);
        to.setBalance(BigDecimal.valueOf(100));

        Mockito.when(ledgerEntryRepository.existsByTransferId("t1")).thenReturn(false);
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        Mockito.when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

        boolean result = ledgerService.transfer("t1", 1L, 2L, BigDecimal.valueOf(50));

        assertThat(result).isTrue();
        assertThat(from.getBalance()).isEqualTo(BigDecimal.valueOf(150));
        assertThat(to.getBalance()).isEqualTo(BigDecimal.valueOf(150));

        Mockito.verify(accountRepository).saveAll(Mockito.anyList());
        Mockito.verify(ledgerEntryRepository).saveAll(Mockito.anyList());
    }

    @Test
    void transfer_failsIfFromAccountNotFound() {
        Mockito.when(ledgerEntryRepository.existsByTransferId("t1")).thenReturn(false);
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> ledgerService.transfer("t1", 1L, 2L, BigDecimal.TEN));
    }

    @Test
    void transfer_failsIfToAccountNotFound() {
        Account from = new Account();
        from.setId(1L);
        from.setBalance(BigDecimal.valueOf(100));

        Mockito.when(ledgerEntryRepository.existsByTransferId("t1")).thenReturn(false);
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        Mockito.when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> ledgerService.transfer("t1", 1L, 2L, BigDecimal.TEN));
    }

    @Test
    void transfer_failsIfInsufficientFunds() {
        Account from = new Account();
        from.setId(1L);
        from.setBalance(BigDecimal.valueOf(20));

        Account to = new Account();
        to.setId(2L);
        to.setBalance(BigDecimal.valueOf(50));

        Mockito.when(ledgerEntryRepository.existsByTransferId("t1")).thenReturn(false);
        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        Mockito.when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(InsufficientFundsException.class,
                () -> ledgerService.transfer("t1", 1L, 2L, BigDecimal.valueOf(100)));
    }

    @Test
    void transfer_isIdempotent() {
        Mockito.when(ledgerEntryRepository.existsByTransferId("t1")).thenReturn(true);

        boolean result = ledgerService.transfer("t1", 1L, 2L, BigDecimal.valueOf(50));

        assertThat(result).isTrue();
        Mockito.verifyNoInteractions(accountRepository);
    }
}
