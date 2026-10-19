package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.Accounting;
import com.pine.pine.models.Company;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.parser.ParseResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ImportService {

    private final AccountService accountService;
    private final CompanyService companyService;

    public ImportService(final AccountService accountService, final CompanyService companyService) {
        this.accountService = accountService;
        this.companyService = companyService;
    }

    @Transactional
    public ParseResult importSnapshot(final Accounting snapshot) {
        if (snapshot instanceof EmberSnapshot) {
            return fromEmber((EmberSnapshot) snapshot);
        }

        if (snapshot instanceof NorthstarSnapshot) {
            return fromNorthStar((NorthstarSnapshot) snapshot);
        }

        if (snapshot instanceof PaperLedgerSnapshot) {
            return fromPaperLedger((PaperLedgerSnapshot) snapshot);
        }
        return new ParseResult("unknown", "INCOMPLETE");
    }

    private ParseResult fromEmber(final EmberSnapshot snapshot) {
        var company = snapshot.getCompany();
        var accounts = snapshot.getAccounts();
        persistEntities(company, "ember", snapshot.getInstallation(), snapshot.getSerial(), accounts);
        return new ParseResult("ember", ParseResult.COMPLETE);
    }

    private ParseResult fromNorthStar(final NorthstarSnapshot snapshot) {
        var company = snapshot.getCompany();
        var accounts = snapshot.getAccounts();
        persistEntities(company, "northstar", snapshot.getConnectionId(), snapshot.getGeneration(), accounts);
        return new ParseResult("north-star", ParseResult.COMPLETE);
    }

    private ParseResult fromPaperLedger(final PaperLedgerSnapshot snapshot) {
        var company = snapshot.getCompany();
        var accounts = snapshot.getAccounts();
        persistEntities(company, "paperledger", snapshot.getSource().connection(), snapshot.getExport().number(), accounts);
        return new ParseResult("paper-ledger", ParseResult.COMPLETE);
    }

    private void persistEntities(final Company company, final String provider, final String connectionId,
                                 final Long revision,
                                 final List<Account> accounts) {
        var persistedCompany = companyService.persistCompany(company);
        var mappedAccounts = accounts.stream().peek(account -> account.setCompany(persistedCompany)).toList();
        accountService.synchronizeAccounts(provider, connectionId, revision, mappedAccounts);
    }
}
