package com.pine.pine.models;

import com.pine.pine.services.AccountAuditLogService;

import java.util.List;

public interface Accounting {
    List<Account> getAccounts(final AccountAuditLogService accountAuditLogService);
    Company getCompany();
}
