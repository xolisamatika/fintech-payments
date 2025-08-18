package com.example.ledger.service;

import com.example.common.model.Account;
import com.example.ledger.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountServiceIT {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void createAndFetchAccount() {
        AccountService accountService = new AccountService(accountRepository);

        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(500));

        Account saved = accountService.createAccount(account);
        assertThat(saved.getId()).isNotNull();

        Optional<Account> fetched = accountService.fetchAccountById(saved.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getBalance()).isEqualTo(BigDecimal.valueOf(500));
    }
}
