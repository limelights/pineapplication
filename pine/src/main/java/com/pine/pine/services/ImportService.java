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
    private final AccountAuditLogService accountAuditLogService;

    public ImportService(final AccountService accountService, final CompanyService companyService, AccountAuditLogService accountAuditLogService) {
        this.accountService = accountService;
        this.companyService = companyService;
        this.accountAuditLogService = accountAuditLogService;
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
        persistEntities(snapshot, "ember", snapshot.getInstallation(), snapshot.getSerial());
        return new ParseResult("ember", ParseResult.COMPLETE);
    }

    private ParseResult fromNorthStar(final NorthstarSnapshot snapshot) {
        persistEntities(snapshot, "northstar", snapshot.getConnectionId(), snapshot.getGeneration());
        return new ParseResult("north-star", ParseResult.COMPLETE);
    }

    private ParseResult fromPaperLedger(final PaperLedgerSnapshot snapshot) {
        persistEntities(snapshot, "paperledger", snapshot.getSource().connection(), snapshot.getExport().number());
        return new ParseResult("paper-ledger", ParseResult.COMPLETE);
    }

    private void persistEntities(final Accounting snapshot, final String provider, final String connectionId,
                                 final Long revision) {
        var parsedAccounts = snapshot.getAccounts(accountAuditLogService);
        var persistedCompany = companyService.persistCompany(snapshot.getCompany());
        var mappedAccounts = parsedAccounts.stream().peek(account -> account.setCompany(persistedCompany)).toList();
        accountService.synchronizeAccounts(provider, connectionId, revision, mappedAccounts);
    }
}
