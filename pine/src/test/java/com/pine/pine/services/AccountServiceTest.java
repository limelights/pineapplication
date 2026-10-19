package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.SnapshotHelper;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private final AccountRepository mockAccountRepository = mock(AccountRepository.class);
    private final AccountAuditLogService mockAccountAuditLogService = mock(AccountAuditLogService.class);

    @Test
    void creates_accounts_from_snapshot_northstar() throws IOException {
        var snap = (NorthstarSnapshot) SnapshotHelper.asSnapshot("northstar-books.7.json", NorthstarSnapshot.class);
        var firstAccountInSnapshot = snap.getProviderAccounts().getFirst();

        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.name(), account.getName());
        assertEquals(firstAccountInSnapshot.id(), account.getId());
    }

    @Test
    void creates_accounts_from_snapshot_ember() throws IOException {
        var snap = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9002.json", EmberSnapshot.class);
        var firstAccountInSnapshot = snap.getLedger().entrySet().iterator().next();

        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.getValue().title(), account.getName());
        assertEquals(firstAccountInSnapshot.getValue().number(), account.getId());
    }

    @Test
    void creates_accounts_from_snapshot_paper_ledger() throws IOException {
        var snap = (PaperLedgerSnapshot) SnapshotHelper.asSnapshot("paper-ledger.43.json", PaperLedgerSnapshot.class);
        var firstAccountInSnapshot = snap.getCatalogue().getFirst();

        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.displayName().name(), account.getName());
        assertEquals(firstAccountInSnapshot.uid(), account.getId());
    }

    @Test
    void keeps_roll_forward_by_revision_protection() throws IOException {
        var snap9001 = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9001.json", EmberSnapshot.class);
        var snap9002 = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9002.json", EmberSnapshot.class);

        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService);
        var a1 = accountService.createAccountFromSnapshot(snap9001);
        var a2 = accountService.createAccountFromSnapshot(snap9002);

        when(mockAccountRepository.findFirstByKeyProviderAndKeyConnectionIdAndKeyExternalIdOrderByKeyRevisionDesc(
                anyString(), anyString(), anyString()
        )).thenReturn(Optional.of(a2));

        accountService.synchronizeAccounts("ember", "company-demo", List.of(a1));

        final ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockAccountAuditLogService, atLeastOnce()).produceAuditLog(accountArgumentCaptor.capture(), stringArgumentCaptor.capture());

        final Account capturedAccount = accountArgumentCaptor.getAllValues().getFirst();
        final String capturedReason = stringArgumentCaptor.getAllValues().getFirst();

        assertEquals("Adding account rejected; higher revision found", capturedReason);
        assertEquals(a1.getRevision(), capturedAccount.getRevision());
        verify(mockAccountAuditLogService, times(2)).produceAuditLog(any(), any());
    }
}
