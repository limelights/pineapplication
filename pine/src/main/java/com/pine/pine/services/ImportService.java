package com.pine.pine.services;

import com.pine.pine.controllers.responses.ImportResultResponse;
import com.pine.pine.models.Accounting;
import com.pine.pine.models.ImportStatus;
import com.pine.pine.models.ImportStatus.Outcome;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.parser.ParseResult;
import com.pine.pine.repositories.ImportStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ImportService {

    private final AccountService accountService;
    private final CompanyService companyService;
    private final AccountAuditLogService accountAuditLogService;
    private final ImportStatusRepository importStatusRepository;
    private final TransactionTemplate transaction;

    public ImportService(final AccountService accountService, final CompanyService companyService,
                         AccountAuditLogService accountAuditLogService, ImportStatusRepository importStatusRepository,
                         PlatformTransactionManager transactionManager) {
        this.accountService = accountService;
        this.companyService = companyService;
        this.accountAuditLogService = accountAuditLogService;
        this.importStatusRepository = importStatusRepository;
        this.transaction = new TransactionTemplate(transactionManager);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ParseResult importSnapshot(final Accounting snapshot) {
        if (snapshot instanceof EmberSnapshot ember) {
            return importSnapshot(ember, "ember", ember.getInstallation(), ember.getSerial(), "ember");
        }

        if (snapshot instanceof NorthstarSnapshot northstar) {
            return importSnapshot(northstar, "northstar", northstar.getConnectionId(), northstar.getGeneration(), "north-star");
        }

        if (snapshot instanceof PaperLedgerSnapshot paperledger) {
            var revision = Optional.ofNullable(paperledger.getExport()).map(PaperLedgerSnapshot.Export::number).orElse(null);
            return importSnapshot(paperledger, "paperledger", paperledger.getSource().connection(), revision, "paper-ledger");
        }
        return new ParseResult("unknown", "INCOMPLETE");
    }

    public List<ImportResultResponse> getImportResults(String companyName) {
        return importStatusRepository.findByCompanyNameOrderByProviderAscConnectionIdAsc(companyName).stream()
                .map(status -> new ImportResultResponse(status.getRunId(), status.getProvider(), status.getConnectionId(),
                        status.getRevision(), status.getStatus(), status.getRanAt(),
                        accountAuditLogService.getFailures(status.getRunId())))
                .toList();
    }

    private ParseResult importSnapshot(Accounting snapshot, String provider, String connectionId,
                                       Long revision, String responseType) {
        var companyName = snapshot.getCompany().getName();
        try (var audit = accountAuditLogService.beginImport()) {
            Outcome outcome;
            try {
                outcome = transaction.execute(ignored -> {
                    var accounts = snapshot.getAccounts(accountAuditLogService);
                    var company = companyService.persistCompany(snapshot.getCompany());
                    accounts.forEach(account -> account.setCompany(company));
                    var result = accountService.synchronizeAccounts(provider, connectionId, revision, accounts);
                    if (result == AccountService.SynchronizationResult.REJECTED || (accounts.isEmpty() && audit.hasFailures())) {
                        return Outcome.FAILURE;
                    }
                    return audit.hasFailures() ? Outcome.PARTIAL : Outcome.SUCCESS;
                });
            } catch (RuntimeException error) {
                audit.addFailure(Optional.ofNullable(error.getMessage()).orElse(error.getClass().getSimpleName()));
                try {
                    saveStatus(companyName, provider, connectionId, revision, Outcome.FAILURE, audit, true);
                } catch (RuntimeException statusError) {
                    error.addSuppressed(statusError);
                }
                throw error;
            }
            saveStatus(companyName, provider, connectionId, revision, outcome, audit, false);
        }
        return new ParseResult(responseType, ParseResult.COMPLETE);
    }

    private void saveStatus(String companyName, String provider, String connectionId, Long revision, Outcome outcome,
                            AccountAuditLogService.ImportAudit audit, boolean rolledBack) {
        // The import transaction has exited; this write survives a failed import without holding its connection.
        transaction.executeWithoutResult(ignored -> {
            if (rolledBack) {
                accountAuditLogService.restoreFailures(audit);
            }
            var status = importStatusRepository.findByCompanyNameAndProviderAndConnectionId(companyName, provider, connectionId)
                    .orElseGet(() -> new ImportStatus(companyName, provider, connectionId));
            status.complete(audit.runId(), revision, outcome, Instant.now());
            importStatusRepository.save(status);
        });
    }
}
