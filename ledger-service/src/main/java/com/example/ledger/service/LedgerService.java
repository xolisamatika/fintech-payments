package com.example.ledger.service;

import com.example.ledger.dto.TransferRequest;
import com.example.ledger.model.Account;
import com.example.ledger.model.LedgerEntry;
import com.example.ledger.model.ProcessedTransfer;
import com.example.ledger.repository.AccountRepository;
import com.example.ledger.repository.LedgerEntryRepository;
import com.example.ledger.repository.ProcessedTransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final ProcessedTransferRepository transferRepository;

    @Transactional
    public boolean applyTransfer(TransferRequest req) {
        if (isAlreadyProcessed(req.transferId())) {
            return transferRepository.findByTransferId(req.transferId())
                    .map(t -> ProcessedTransfer.Status.SUCCESS.equals(t.getStatus()))
                    .orElse(false);
        }

        validateAmount(req.amount());
        List<Account> accounts = lockAccounts(req.fromAccountId(), req.toAccountId());
        Account from = accounts.stream().filter(a -> a.getId().equals(req.fromAccountId())).findFirst().orElseThrow();
        Account to   = accounts.stream().filter(a -> a.getId().equals(req.toAccountId())).findFirst().orElseThrow();

        if (from.getBalance().compareTo(req.amount()) < 0) {
            recordFailure(req.transferId(), "INSUFFICIENT_FUNDS");
            return false;
        }

        transferFunds(from, to, req.amount());
        recordLedgerEntries(req, from.getId(), to.getId(), req.amount());
        recordSuccess(req.transferId());
        return true;
    }

    private boolean isAlreadyProcessed(String transferId) {
        return transferRepository.findByTransferId(transferId).isPresent();
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount > 0 required");
    }

    private List<Account> lockAccounts(Long id1, Long id2) {
        return List.of(
                accountRepository.findById(Math.min(id1, id2)).orElseThrow(),
                accountRepository.findById(Math.max(id1, id2)).orElseThrow()
        );
    }

    private void transferFunds(Account from, Account to, BigDecimal amount) {
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);
    }

    private void recordLedgerEntries(TransferRequest req, Long fromId, Long toId, BigDecimal amount) {
        ledgerEntryRepository.save(entry(req, fromId, amount, LedgerEntry.EntryType.DEBIT));
        ledgerEntryRepository.save(entry(req, toId,   amount, LedgerEntry.EntryType.CREDIT));
    }

    private LedgerEntry entry(TransferRequest r, Long accountId, BigDecimal amount, LedgerEntry.EntryType type) {
        LedgerEntry e = new LedgerEntry();
        e.setTransferId(r.transferId());
        e.setAccountId(accountId);
        e.setAmount(amount);
        e.setType(type);
        return e;
    }

    private void recordFailure(String transferId, String error) {
        ProcessedTransfer transfer = new ProcessedTransfer();
        transfer.setTransferId(transferId);
        transfer.setStatus(ProcessedTransfer.Status.FAILED);
        transfer.setError(error);
        transfer.setCreatedAt(Instant.now());
        transferRepository.save(transfer);
    }

    private void recordSuccess(String transferId) {
        ProcessedTransfer transfer = new ProcessedTransfer();
        transfer.setTransferId(transferId);
        transfer.setStatus(ProcessedTransfer.Status.SUCCESS);
        transfer.setCreatedAt(Instant.now());
        transferRepository.save(transfer);
    }
}
