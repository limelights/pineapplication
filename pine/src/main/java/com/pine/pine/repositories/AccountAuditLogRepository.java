package com.pine.pine.repositories;


import com.pine.pine.models.Account;
import com.pine.pine.models.AccountAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountAuditLogRepository extends JpaRepository<AccountAuditLog, Long> {
    List<AccountAuditLog> findByAccount(final Account account);

    List<AccountAuditLog> findByRunIdAndAccountIsNullOrderByIdAsc(String runId);
}

