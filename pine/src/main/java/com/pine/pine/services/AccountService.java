package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;

public class AccountService {
    public Account createAccountFromSnapshot(final EmberSnapshot snapshot) {
        var ledgerAccount = snapshot.ledger().entrySet().iterator().next();
        var account = ledgerAccount.getValue();
        return createAccount(account.number(), account.title());
    }

    public Account createAccountFromSnapshot(final NorthstarSnapshot snapshot) {
        var account = snapshot.accounts().getFirst();
        return createAccount(account.id(), account.name());
    }

    public Account createAccountFromSnapshot(final PaperLedgerSnapshot snapshot) {
        var account = snapshot.catalogue().getFirst();
        return createAccount(account.uid(), account.displayName().name());
    }

    private Account createAccount(String id, String name) {
        return new Account(name, id);
    }
}
