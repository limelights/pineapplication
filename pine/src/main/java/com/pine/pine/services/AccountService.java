package com.pine.pine.services;

import com.pine.pine.models.Account;
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

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
        var removedAccounts = accountRepository.findByKeyProviderAndKeyConnectionId(provider, connectionId).stream()
                .filter(account -> !incomingKeys.contains(account.getKey()))
                .toList();

        // Ember stores an archive flag; the other providers store an active flag.
        removedAccounts.forEach(account -> account.setStatus("ember".equals(provider)));
        accountRepository.saveAll(removedAccounts);
        accountRepository.saveAll(accounts);
    }

    public List<Account> getByCompanyId(final Company company) {
        return accountRepository.findByCompany(company,
                Sort.by("key.provider", "key.connectionId", "key.externalId"));
    }
}
