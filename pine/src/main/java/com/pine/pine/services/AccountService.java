package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.AccountId;
import com.pine.pine.models.Company;
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
    private final AccountRepository accountRepository;

    private final AccountAuditLogService accountAuditLogService;

    public AccountService(final AccountRepository accountRepository, final AccountAuditLogService accountAuditLogService) {
        this.accountRepository = accountRepository;
        this.accountAuditLogService = accountAuditLogService;
    }

    public Account createAccountFromSnapshot(final EmberSnapshot snapshot) {
        return snapshot.getAccounts().getFirst();
    }

    public Account createAccountFromSnapshot(final NorthstarSnapshot snapshot) {
        return snapshot.getAccounts().getFirst();
    }

    public Account createAccountFromSnapshot(final PaperLedgerSnapshot snapshot) {
        return snapshot.getAccounts().getFirst();
    }

    @Transactional
    public void synchronizeAccounts(String provider, String connectionId, List<Account> accounts) {
        var incomingKeys = accounts.stream()
                .map(Account::getKey)
                .collect(Collectors.toSet());
        if (accountRepository != null) {
            var accountsScheduledForRemoval = accountRepository.findByKeyProviderAndKeyConnectionId(provider, connectionId).stream()
                    .filter(account -> !incomingKeys.contains(account.getKey()))
                    .toList();

            // Ember stores an archive flag; the other providers store an active flag.
            accountsScheduledForRemoval.forEach(account -> account.setStatus("ember".equals(provider)));

            // Todo: Use transaction here to roll back auditlogs, if needed together with failed accounting.

            accounts.forEach(account -> {

                var latestRevisionsOfAccount =
                        accountRepository.findFirstByKeyProviderAndKeyConnectionIdAndKeyExternalIdOrderByKeyRevisionDesc(
                                provider, connectionId, account.getKey().getExternalId()
                        );
                latestRevisionsOfAccount.ifPresent(value -> account.setDescription(value.getDescription()));
            });


            accountRepository.saveAll(accountsScheduledForRemoval);
            accountRepository.saveAll(accounts);

            accountsScheduledForRemoval.forEach(account -> {
                accountAuditLogService.produceAuditLog(account, "Service detected slated for removal");
            });

            accounts.forEach(a -> {
                accountAuditLogService.produceAuditLog(a, "Service ingested account successfully");
            });
        }
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
