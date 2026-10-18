package com.pine.pine.services;

import com.pine.pine.models.Company;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.parser.ParseResult;
import org.springframework.stereotype.Service;

@Service
public class ImportService {

    private final AccountService accountService;
    private final CompanyService companyService;

    public ImportService(final AccountService accountService, final CompanyService companyService) {
        this.accountService = accountService;
        this.companyService = companyService;
    }

    public ParseResult fromEmber(final EmberSnapshot snapshot) {
        var name = snapshot.installation();
        var company = new Company(name);
        persistEntities(company);
        return new ParseResult("ember", ParseResult.COMPLETE);
    }

    public ParseResult fromNorthStar(NorthstarSnapshot snapshot) {
        var name = snapshot.connectionId();
        var company = new Company(name);
        persistEntities(company);
        return new ParseResult("north-star", ParseResult.COMPLETE);
    }

    public ParseResult fromPaperLedger(PaperLedgerSnapshot snapshot) {
        var name = snapshot.source().connection();
        var company = new Company(name);
        persistEntities(company);
        return new ParseResult("paper-ledger", ParseResult.COMPLETE);
    }

    private void persistEntities(final Company company) {
        companyService.persistCompany(company);
    }
}
