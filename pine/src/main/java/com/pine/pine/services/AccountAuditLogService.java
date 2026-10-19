package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.AccountAuditLog;
import com.pine.pine.repositories.AccountAuditLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AccountAuditLogService {

    private final AccountAuditLogRepository accountAuditLogRepository;
    private final ThreadLocal<ImportAudit> currentImport = new ThreadLocal<>();

    public AccountAuditLogService(AccountAuditLogRepository accountAuditLogRepository) {
        this.accountAuditLogRepository = accountAuditLogRepository;
    }

    public List<AccountAuditLog> getAll() {
        return accountAuditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "account.key.revision", "id"));
    }

    public void produceAuditLog(final Account account, final String reason) {
        final AccountAuditLog logEntry = new AccountAuditLog(account, reason);
        var audit = currentImport.get();
        if (audit != null) {
            logEntry.setRunId(audit.runId);
            if (account == null) {
                audit.addFailure(reason);
            }
        }
        accountAuditLogRepository.save(logEntry);
    }

    public List<String> getFailures(String runId) {
        return accountAuditLogRepository.findByRunIdAndAccountIsNullOrderByIdAsc(runId).stream()
                .map(AccountAuditLog::getReason).toList();
    }

    ImportAudit beginImport() {
        var audit = new ImportAudit();
        currentImport.set(audit);
        return audit;
    }

    void restoreFailures(ImportAudit audit) {
        audit.failures.forEach(reason -> {
            var entry = new AccountAuditLog(null, reason);
            entry.setRunId(audit.runId);
            accountAuditLogRepository.save(entry);
        });
    }

    // Keep failure reasons in memory until the transaction has committed or rolled back.
    final class ImportAudit implements AutoCloseable {
        private final String runId = UUID.randomUUID().toString();
        private final List<String> failures = new ArrayList<>();

        String runId() { return runId; }
        boolean hasFailures() { return !failures.isEmpty(); }
        void addFailure(String reason) { failures.add(reason); }

        @Override
        public void close() {
            currentImport.remove();
        }
    }
}
