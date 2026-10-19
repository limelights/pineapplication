package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.Company;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccountFromSnapshot(final EmberSnapshot snapshot) {
        var ledgerAccount = snapshot.getLedger().entrySet().iterator().next();
        var account = ledgerAccount.getValue();
        return createAccount(account.number(), account.title());
    }

    public Account createAccountFromSnapshot(final NorthstarSnapshot snapshot) {
        var account = snapshot.getAccounts().getFirst();
        return createAccount(account.getId(), account.getName());
    }

    public Account createAccountFromSnapshot(final PaperLedgerSnapshot snapshot) {
        var account = snapshot.getCatalogue().getFirst();
        return createAccount(account.uid(), account.displayName().name());
    }

    private Account createAccount(String id, String name) {
        return new Account(name, id);
    }

    public void persistAccounts(List<Account> accounts) {
        accountRepository.saveAll(accounts);
    }

    public List<Account> getByCompanyId(final Company company) {
        return accountRepository.findByCompanyOrderByUidAsc(company);
    }
}
