package com.pine.pine.services;

import com.pine.pine.models.SnapshotHelper;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.repositories.AccountRepository;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class AccountServiceTest {

    private AccountRepository accountRepository = mock(AccountRepository.class);
    private AccountAuditLogService accountAuditLogService = mock(AccountAuditLogService.class);

    @Test
    void creates_accounts_from_snapshot_northstar() throws IOException {
        var snap = (NorthstarSnapshot) SnapshotHelper.asSnapshot("northstar-books.7.json", NorthstarSnapshot.class);
        var firstAccountInSnapshot = snap.getProviderAccounts().getFirst();

        var accountService = new AccountService(accountRepository, accountAuditLogService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.name(), account.getName());
        assertEquals(firstAccountInSnapshot.id(), account.getId());
    }

    @Test
    void creates_accounts_from_snapshot_ember() throws IOException {
        var snap = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9002.json", EmberSnapshot.class);
        var firstAccountInSnapshot = snap.getLedger().entrySet().iterator().next();

        var accountService = new AccountService(accountRepository, accountAuditLogService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.getValue().title(), account.getName());
        assertEquals(firstAccountInSnapshot.getValue().number(), account.getId());
    }

    @Test
    void creates_accounts_from_snapshot_paper_ledger() throws IOException {
        var snap = (PaperLedgerSnapshot) SnapshotHelper.asSnapshot("paper-ledger.43.json", PaperLedgerSnapshot.class);
        var firstAccountInSnapshot = snap.getCatalogue().getFirst();

        var accountService = new AccountService(accountRepository, accountAuditLogService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.displayName().name(), account.getName());
        assertEquals(firstAccountInSnapshot.uid(), account.getId());
    }
}
