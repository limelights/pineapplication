package com.pine.pine.services;

import com.pine.pine.models.RevisionState;
import com.pine.pine.models.SnapshotHelper;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.repositories.AccountRepository;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private final AccountRepository mockAccountRepository = mock(AccountRepository.class);
    private final AccountAuditLogService mockAccountAuditLogService = mock(AccountAuditLogService.class);
    private final RevisionStateService mockRevisionStateService = mock(RevisionStateService.class);

    @Test
    void creates_accounts_from_snapshot_northstar() throws IOException {
        var snap = (NorthstarSnapshot) SnapshotHelper.asSnapshot("northstar-books.7.json", NorthstarSnapshot.class);
        var firstAccountInSnapshot = snap.getProviderAccounts().getFirst();

        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService, mockRevisionStateService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.name(), account.getName());
        assertEquals(firstAccountInSnapshot.id(), account.getId());
    }

    @Test
    void creates_accounts_from_snapshot_ember() throws IOException {
        var snap = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9002.json", EmberSnapshot.class);
        var firstAccountInSnapshot = snap.getLedger().entrySet().iterator().next();

        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService, mockRevisionStateService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.getValue().title(), account.getName());
        assertEquals(firstAccountInSnapshot.getValue().number(), account.getId());
    }

    @Test
    void creates_accounts_from_snapshot_paper_ledger() throws IOException {
        var snap = (PaperLedgerSnapshot) SnapshotHelper.asSnapshot("paper-ledger.43.json", PaperLedgerSnapshot.class);
        var firstAccountInSnapshot = snap.getCatalogue().getFirst();

        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService, mockRevisionStateService);
        var account = accountService.createAccountFromSnapshot(snap);
        assertEquals(firstAccountInSnapshot.displayName().name(), account.getName());
        assertEquals(firstAccountInSnapshot.uid(), account.getId());
    }

    @Test
    void keeps_roll_forward_by_revision_protection() throws IOException {
        var snap9001 = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9001.json", EmberSnapshot.class);
        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService, mockRevisionStateService);
        var a1 = accountService.createAccountFromSnapshot(snap9001);
        var acceptedState = new RevisionState("ember", "company-demo");
        acceptedState.setRevision(9002L);
        when(mockRevisionStateService.findByProviderAndConnectionId("ember", "company-demo"))
                .thenReturn(Optional.of(acceptedState));

        assertEquals(AccountService.SynchronizationResult.REJECTED,
                accountService.synchronizeAccounts("ember", "company-demo", 9001L, List.of(a1)));

        verify(mockAccountAuditLogService).produceAuditLog(
                null, "Rejected ember/company-demo revision 9001: latest accepted revision is 9002");
        verifyNoMoreInteractions(mockAccountAuditLogService);
        verifyNoInteractions(mockAccountRepository);
        verify(mockRevisionStateService, never()).saveRevisionState(any());
        assertEquals(9002L, acceptedState.getLatestRevision());
    }

    @Test
    void rejects_for_next_iteration() throws IOException {
        var snap9001 = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9001.json", EmberSnapshot.class);
        var snap9002 = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9002.json", EmberSnapshot.class);

        var accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService, mockRevisionStateService);
        var a1 = accountService.createAccountFromSnapshot(snap9001);
        var a2 = accountService.createAccountFromSnapshot(snap9002);
        var savedState = new AtomicReference<RevisionState>();
        when(mockRevisionStateService.findByProviderAndConnectionId("ember", "company-demo"))
                .thenAnswer(invocation -> Optional.ofNullable(savedState.get()));
        doAnswer(invocation -> {
            savedState.set(invocation.getArgument(0));
            return null;
        }).when(mockRevisionStateService).saveRevisionState(any());

        assertEquals(AccountService.SynchronizationResult.ACCEPTED,
                accountService.synchronizeAccounts("ember", "company-demo", 9002L, List.of(a2)));

        assertNotNull(savedState.get());
        assertEquals("ember", savedState.get().getProvider());
        assertEquals("company-demo", savedState.get().getConnectionId());
        assertEquals(9002L, savedState.get().getLatestRevision());
        verify(mockRevisionStateService).saveRevisionState(savedState.get());
        verify(mockAccountRepository).saveAll(List.of(a2));

        clearInvocations(mockAccountRepository, mockAccountAuditLogService, mockRevisionStateService);

        assertEquals(AccountService.SynchronizationResult.REJECTED,
                accountService.synchronizeAccounts("ember", "company-demo", 9001L, List.of(a1)));

        verify(mockAccountAuditLogService).produceAuditLog(
                null, "Rejected ember/company-demo revision 9001: latest accepted revision is 9002");
        verifyNoMoreInteractions(mockAccountAuditLogService);
        verifyNoInteractions(mockAccountRepository);
        verify(mockRevisionStateService, never()).saveRevisionState(any());
        assertEquals(9002L, savedState.get().getLatestRevision());
    }
}
