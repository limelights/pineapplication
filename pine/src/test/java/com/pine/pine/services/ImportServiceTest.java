package com.pine.pine.services;

import com.pine.pine.models.SnapshotHelper;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.repositories.CompanyRepository;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ImportServiceTest {

    @Test
    void importPayloadsYieldsEntities() throws IOException {
        final CompanyRepository mockCompanyRepository = null;
        final AccountService accountService = new AccountService();
        final CompanyService companyService = new CompanyService(mockCompanyRepository);

        var snap = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9001.json", EmberSnapshot.class);


        final ImportService importService = new ImportService(accountService, companyService);
        importService.fromEmber(snap);
    }
}
