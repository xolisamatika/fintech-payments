package com.example.ledger.service;

import com.example.common.dto.TransferRequest;
import com.example.common.model.Account;
import com.example.common.model.LedgerEntry;
import com.example.common.model.ProcessedTransfer;
import com.example.ledger.repository.AccountRepository;
import com.example.ledger.repository.LedgerEntryRepository;
import com.example.ledger.repository.ProcessedTransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LedgerServiceTest {

    private AccountRepository accountRepo;
    private LedgerEntryRepository ledgerRepo;
    private ProcessedTransferRepository transferRepo;
    private LedgerService service;

    @BeforeEach
    void setUp() {
        accountRepo = mock(AccountRepository.class);
        ledgerRepo = mock(LedgerEntryRepository.class);
        transferRepo = mock(ProcessedTransferRepository.class);
        service = new LedgerService(accountRepo, ledgerRepo, transferRepo);
    }

    @Test
    void applyTransfer_successful() {
        var from = new Account(1L, 0L, BigDecimal.valueOf(100));
        var to = new Account(2L, 0L,BigDecimal.valueOf(50));

        when(accountRepo.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepo.findById(2L)).thenReturn(Optional.of(to));
        when(transferRepo.findByTransferId("transId")).thenReturn(Optional.empty());

        TransferRequest req = new TransferRequest("transId", 1L, 2L, BigDecimal.valueOf(20));
        boolean result = service.applyTransfer(req);

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(80), from.getBalance());
        assertEquals(BigDecimal.valueOf(70), to.getBalance());
        verify(ledgerRepo, times(2)).save(any(LedgerEntry.class));
        verify(transferRepo).save(argThat(t -> t.getStatus() == ProcessedTransfer.Status.SUCCESS));
    }

    @Test
    void applyTransfer_insufficientFunds() {
        var from = new Account(1L, 0L, BigDecimal.valueOf(10));
        var to = new Account(2L, 0L, BigDecimal.valueOf(50));

        when(accountRepo.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepo.findById(2L)).thenReturn(Optional.of(to));
        when(transferRepo.findByTransferId("transId")).thenReturn(Optional.empty());

        TransferRequest req = new TransferRequest("transId", 1L, 2L, BigDecimal.valueOf(20));
        boolean result = service.applyTransfer(req);

        assertFalse(result);
        verify(transferRepo).save(argThat(t -> t.getStatus() == ProcessedTransfer.Status.FAILED));
    }

    @Test
    void applyTransfer_duplicateTransfer_returnsPreviousResult() {
        ProcessedTransfer existing = new ProcessedTransfer();
        existing.setStatus(ProcessedTransfer.Status.SUCCESS);
        when(transferRepo.findByTransferId("transId")).thenReturn(Optional.of(existing));

        TransferRequest req = new TransferRequest("transId", 1L, 2L, BigDecimal.valueOf(20));
        boolean result = service.applyTransfer(req);

        assertTrue(result);
        verifyNoInteractions(accountRepo);
    }

    @Test
    void applyTransfer_invalidAmount_throwsException() {
        TransferRequest req = new TransferRequest("transId", 1L, 2L, BigDecimal.ZERO);
        when(transferRepo.findByTransferId("transId")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.applyTransfer(req));
    }
}
