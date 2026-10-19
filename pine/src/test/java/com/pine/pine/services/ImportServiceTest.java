package com.pine.pine.services;

import com.pine.pine.models.SnapshotHelper;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.repositories.AccountRepository;
import com.pine.pine.repositories.CompanyRepository;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;

public class ImportServiceTest {

    @Test
    void importPayloadsYieldsEntities() throws IOException {
        final AccountRepository mockAccountRepository = mock(AccountRepository.class);
        final CompanyRepository mockCompanyRepository = mock(CompanyRepository.class);
        final AccountAuditLogService mockAccountAuditLogService = mock(AccountAuditLogService.class);
        final AccountService accountService = new AccountService(mockAccountRepository, mockAccountAuditLogService);
        final CompanyService companyService = new CompanyService(mockCompanyRepository);

        var snap = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9001.json", EmberSnapshot.class);


        final ImportService importService = new ImportService(accountService, companyService);
        importService.importSnapshot(snap);
    }
}
