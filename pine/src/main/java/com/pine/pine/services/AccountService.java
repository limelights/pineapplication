package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.Company;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.repositories.AccountRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void persistAccounts(List<Account> accounts) {
        accountRepository.saveAll(accounts);
    }

    public List<Account> getByCompanyId(final Company company) {
        return accountRepository.findByCompany(company,
                Sort.by("key.provider", "key.connectionId", "key.externalId"));
    }
}
