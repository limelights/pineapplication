package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.AccountId;
import com.pine.pine.models.Company;
import com.pine.pine.models.RevisionState;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.repositories.AccountRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    public enum SynchronizationResult { ACCEPTED, REJECTED }

    private final AccountRepository accountRepository;

    private final AccountAuditLogService accountAuditLogService;
    private final RevisionStateService revisionStateService;

    public AccountService(final AccountRepository accountRepository, final AccountAuditLogService accountAuditLogService,
                          final RevisionStateService revisionStateService) {
        this.accountRepository = accountRepository;
        this.accountAuditLogService = accountAuditLogService;
        this.revisionStateService = revisionStateService;
    }

    public Account createAccountFromSnapshot(final EmberSnapshot snapshot) {
        return snapshot.getAccounts(accountAuditLogService).getFirst();
    }

    public Account createAccountFromSnapshot(final NorthstarSnapshot snapshot) {
        return snapshot.getAccounts(accountAuditLogService).getFirst();
    }

    public Account createAccountFromSnapshot(final PaperLedgerSnapshot snapshot) {
        return snapshot.getAccounts(accountAuditLogService).getFirst();
    }

    @Transactional
    public SynchronizationResult synchronizeAccounts(
            final String provider, final String connectionId, final Long incomingRevision, final List<Account> accounts) {
        if (incomingRevision == null) {
            throw new IllegalArgumentException("Snapshot revision must not be null");
        }

        var revisionState = revisionStateService.findByProviderAndConnectionId(provider, connectionId)
                .orElseGet(() -> new RevisionState(provider, connectionId));

        Long latestRevision = revisionState.getLatestRevision();
        if (latestRevision == null) {
            latestRevision = accountRepository.findByKeyProviderAndKeyConnectionId(provider, connectionId).stream()
                    .map(Account::getRevision)
                    .max(Long::compareTo)
                    .orElse(null);
        }

        if (latestRevision != null && incomingRevision < latestRevision) {
            accountAuditLogService.produceAuditLog(null,
                    "Rejected %s/%s revision %d: latest accepted revision is %d"
                            .formatted(provider, connectionId, incomingRevision, latestRevision));
            return SynchronizationResult.REJECTED;
        }

        var incomingKeys = accounts.stream()
                .map(Account::getKey)
                .collect(Collectors.toSet());
        var accountsScheduledForRemoval = accountRepository.findByKeyProviderAndKeyConnectionId(provider, connectionId).stream()
                .filter(account -> !incomingKeys.contains(account.getKey()))
                .toList();

        // Ember stores an archive flag; the other providers store an active flag.
        accountsScheduledForRemoval.forEach(account -> account.setStatus("ember".equals(provider)));

        accounts.forEach(account ->
                accountRepository.findFirstByKeyProviderAndKeyConnectionIdAndKeyExternalIdOrderByKeyRevisionDesc(
                                provider, connectionId, account.getKey().getExternalId())
                        .ifPresent(latestAccount -> account.setDescription(latestAccount.getDescription())));

        accountRepository.saveAll(accountsScheduledForRemoval);
        accountRepository.saveAll(accounts);

        accountsScheduledForRemoval.forEach(account -> {
            accountAuditLogService.produceAuditLog(account, "Service detected slated for removal");
        });

        accounts.forEach(account -> {
            accountAuditLogService.produceAuditLog(account, "Service ingested account successfully");
        });

        revisionState.setRevision(incomingRevision);
        revisionStateService.saveRevisionState(revisionState);
        return SynchronizationResult.ACCEPTED;
    }

    public List<Account> getByCompanyId(final Company company) {
        return accountRepository.findByCompany(company,
                Sort.by(Sort.Direction.DESC, "key.revision")
                        .and(Sort.by("key.provider", "key.connectionId", "key.externalId")));
    }

    public Account getByAccountId(final AccountId accountId) {
        return accountRepository.getByKey(accountId);
    }
}
