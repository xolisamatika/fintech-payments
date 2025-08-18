package com.example.ledger.service;

import com.example.common.model.Account;
import com.example.ledger.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class AccountServiceTest {

    private final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
    private final AccountService accountService = new AccountService(accountRepository);

    @Test
    void createAccount_savesAccount() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));

        Mockito.when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account a = invocation.getArgument(0);
            a.setId(1L);
            a.setVersion(0L);
            return a;
        });

        Account saved = accountService.createAccount(account);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getBalance()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(saved.getVersion()).isEqualTo(0L);
    }

    @Test
    void fetchAccountById_returnsOptional() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.TEN);

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.fetchAccountById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getBalance()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void fetchAccountById_returnsEmptyForUnknownId() {
        Mockito.when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.fetchAccountById(2L);

        assertThat(result).isEmpty();
    }
}
