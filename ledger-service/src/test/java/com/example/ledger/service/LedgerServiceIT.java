package com.example.ledger.service;

import com.example.common.model.Account;
import com.example.common.model.LedgerEntry;
import com.example.ledger.repository.AccountRepository;
import com.example.ledger.repository.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LedgerServiceIT {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LedgerEntryRepository ledgerEntryRepository;

    @Test
    void transfer_updatesBalancesAndLedgerEntries() {
        Account from = new Account();
        from.setBalance(BigDecimal.valueOf(200));
        accountRepository.save(from);

        Account to = new Account();
        to.setBalance(BigDecimal.valueOf(50));
        accountRepository.save(to);

        boolean result = ledgerService.transfer("tx1", from.getId(), to.getId(), BigDecimal.valueOf(75));

        assertThat(result).isTrue();

        Account updatedFrom = accountRepository.findById(from.getId()).get();
        Account updatedTo = accountRepository.findById(to.getId()).get();

        assertThat(updatedFrom.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(125));
        assertThat(updatedTo.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(125));

        List<LedgerEntry> entries = ledgerEntryRepository.findAll();
        assertThat(entries).hasSize(2);
        assertThat(entries).extracting("type")
                .containsExactlyInAnyOrder(LedgerEntry.EntryType.DEBIT, LedgerEntry.EntryType.CREDIT);
    }
}
