package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.Accounting;
import com.pine.pine.models.Company;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.parser.ParseResult;
import org.springframework.stereotype.Service;

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
        var name = snapshot.getInstallation();
        var company = new Company(name);
        var accounts = snapshot.getAccounts();
        persistEntities(company, snapshot.getAccounts());
        return new ParseResult("ember", ParseResult.COMPLETE);
    }

    private ParseResult fromNorthStar(final NorthstarSnapshot snapshot) {
        var name = snapshot.getConnectionId();
        var company = new Company(name);
        persistEntities(company, Collections.emptyList());
        return new ParseResult("north-star", ParseResult.COMPLETE);
    }

    private ParseResult fromPaperLedger(final PaperLedgerSnapshot snapshot) {
        var name = snapshot.getSource().connection();
        var company = new Company(name);
        persistEntities(company, Collections.emptyList());
        return new ParseResult("paper-ledger", ParseResult.COMPLETE);
    }

    private void persistEntities(final Company company, final List<Account> accounts) {
        companyService.persistCompany(company);
    }
}
