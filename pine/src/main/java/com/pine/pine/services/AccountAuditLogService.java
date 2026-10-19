package com.pine.pine.services;

import com.pine.pine.models.Account;
import com.pine.pine.models.AccountAuditLog;
import com.pine.pine.repositories.AccountAuditLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountAuditLogService {

    private final AccountAuditLogRepository accountAuditLogRepository;

    public AccountAuditLogService(AccountAuditLogRepository accountAuditLogRepository) {
        this.accountAuditLogRepository = accountAuditLogRepository;
    }

    public List<AccountAuditLog> getAll() {
        return accountAuditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    void produceAuditLog(final Account account, final String reason) {
        final AccountAuditLog logEntry = new AccountAuditLog(account, reason);
        accountAuditLogRepository.save(logEntry);
    }
}
