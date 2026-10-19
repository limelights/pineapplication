package com.pine.pine.services;

import com.pine.pine.controllers.responses.ImportResultResponse;
import com.pine.pine.models.Accounting;
import com.pine.pine.models.SnapshotHelper;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.pine.pine.models.ImportStatus.Outcome.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:file:import-status-tests?mode=memory&cache=shared",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ImportServiceTest {
    @Autowired private ImportService importService;
    @Autowired private AccountAuditLogService auditLogService;
    @Autowired private AccountAuditLogRepository auditLogs;
    @Autowired private ImportStatusRepository statuses;
    @Autowired private AccountRepository accounts;
    @Autowired private RevisionStateRepository revisions;
    @Autowired private CompanyRepository companies;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void resetDatabase() {
        auditLogs.deleteAll();
        statuses.deleteAll();
        accounts.deleteAll();
        revisions.deleteAll();
        companies.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @ParameterizedTest
    @CsvSource({
            "ember-accounting.9001.json, ember, ember, 9001",
            "northstar-books.7.json, northstar, north-star, 7",
            "paper-ledger.41.json, paperledger, paper-ledger, 41"
    })
    void recordsSuccessfulImports(String fixture, String provider, String responseType, long revision) throws Exception {
        var before = Instant.now();
        var result = importService.importSnapshot(snapshot(fixture));
        var status = latest();

        assertEquals("complete", result.getStatus());
        assertEquals(responseType, result.getType());
        assertEquals(provider, status.provider());
        assertEquals("company-demo", status.connectionId());
        assertEquals(revision, status.revision());
        assertEquals(SUCCESS, status.status());
        assertEquals(List.of(), status.failures());
        assertDoesNotThrow(() -> UUID.fromString(status.runId()));
        assertFalse(status.ranAt().isBefore(before));
        assertFalse(status.ranAt().isAfter(Instant.now()));
        assertTrue(auditLogs.findAll().stream().allMatch(log -> status.runId().equals(log.getRunId())));

        var json = objectMapper.readTree(mvc.perform(get("/api/snapshots/company-demo/status"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(7, json.get(0).size());
        assertEquals(status.runId(), json.get(0).get("runId").asString());
        assertEquals("SUCCESS", json.get(0).get("status").asString());
        assertEquals(status.ranAt(), Instant.parse(json.get(0).get("ranAt").asString()));
    }

    @Test
    void acceptsAnEmptySnapshot() throws IOException {
        var snapshot = (EmberSnapshot) snapshot("ember-accounting.9001.json");
        snapshot.setLedger(java.util.Map.of());

        importService.importSnapshot(snapshot);

        assertEquals(SUCCESS, latest().status());
        assertEquals(List.of(), latest().failures());
        assertEquals(0, accounts.count());
        assertEquals(9001L, revisions.findAll().getFirst().getLatestRevision());
    }

    @Test
    void reportsPartialImportUsingExistingFailureReasons() throws IOException {
        importService.importSnapshot(snapshot("paper-ledger.41.json"));
        importService.importSnapshot(snapshot("paper-ledger.44.json"));

        var status = latest();
        assertEquals(PARTIAL, status.status());
        assertEquals(44L, status.revision());
        assertEquals(List.of("Account `Record without a stable identifier` was not able to be ingested"), status.failures());
        assertEquals(1, accounts.findAll().stream().filter(account -> account.getRevision() == 44L).count());
        assertTrue(accounts.findAll().stream().filter(account -> account.getRevision() == 41L)
                .noneMatch(com.pine.pine.models.Account::isStatus));
    }

    @Test
    void reportsFailureWhenEveryAccountFailsWithoutChangingRemovalPolicy() throws IOException {
        importService.importSnapshot(snapshot("paper-ledger.41.json"));
        var snapshot = (PaperLedgerSnapshot) snapshot("paper-ledger.44.json");
        snapshot.setCatalogue(snapshot.getCatalogue().stream().filter(entry -> entry.uid() == null).toList());

        importService.importSnapshot(snapshot);

        assertEquals(FAILURE, latest().status());
        assertEquals(1, latest().failures().size());
        assertTrue(accounts.findAll().stream().noneMatch(com.pine.pine.models.Account::isStatus));
        assertEquals(44L, revisions.findAll().getFirst().getLatestRevision());
    }

    @Test
    void recordsRejectedRevisionAsLatestAttempt() throws IOException {
        importService.importSnapshot(snapshot("ember-accounting.9002.json"));
        var previousRun = latest().runId();

        var response = importService.importSnapshot(snapshot("ember-accounting.9001.json"));

        assertEquals("complete", response.getStatus());
        assertEquals(FAILURE, latest().status());
        assertEquals(9001L, latest().revision());
        assertNotEquals(previousRun, latest().runId());
        assertEquals(List.of("Rejected ember/company-demo revision 9001: latest accepted revision is 9002"), latest().failures());
        assertEquals(1, statuses.count());
        assertEquals(9002L, revisions.findAll().getFirst().getLatestRevision());
        assertTrue(accounts.findAll().stream().allMatch(account -> account.getRevision() == 9002L));
    }

    @Test
    void retrievesOnlyLatestStatusesForTheRequestedCompany() throws Exception {
        importService.importSnapshot(snapshot("ember-accounting.9001.json"));
        importService.importSnapshot(snapshot("ember-accounting.9002.json"));
        importService.importSnapshot(snapshot("northstar-books.7.json"));
        var otherCompany = (EmberSnapshot) snapshot("ember-accounting.9001.json");
        otherCompany.setInstallation("other-company");
        importService.importSnapshot(otherCompany);

        assertEquals(3, statuses.count());
        assertEquals(2, importService.getImportResults("company-demo").size());
        mvc.perform(get("/api/snapshots/company-demo/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].provider").value("ember"))
                .andExpect(jsonPath("$[0].revision").value(9002))
                .andExpect(jsonPath("$[1].provider").value("northstar"));
        mvc.perform(get("/api/snapshots/other-company/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].connectionId").value("other-company"));
    }

    @Test
    void returnsEmptyWithoutReconstructingHistoricalImports() throws Exception {
        auditLogService.produceAuditLog(null, "Historical failure");
        assertNull(auditLogs.findAll().getFirst().getRunId());

        mvc.perform(get("/api/snapshots/company-demo/status"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void savesFailureAndReasonsAfterDatabaseRollback() throws IOException {
        jdbc.execute("CREATE TRIGGER fail_revision BEFORE INSERT ON revision_states "
                + "BEGIN SELECT RAISE(ABORT, 'forced revision failure'); END");
        try {
            assertThrows(RuntimeException.class, () -> importService.importSnapshot(snapshot("paper-ledger.44.json")));
        } finally {
            jdbc.execute("DROP TRIGGER fail_revision");
        }

        var status = latest();
        assertEquals(FAILURE, status.status());
        assertEquals(44L, status.revision());
        assertEquals(0, accounts.count());
        assertEquals(0, companies.count());
        assertEquals(0, revisions.count());
        assertEquals(2, status.failures().size());
        assertEquals("Account `Record without a stable identifier` was not able to be ingested", status.failures().getFirst());
        assertTrue(status.failures().getLast().contains("forced revision failure"));
        assertEquals(2, auditLogs.count());

        importService.importSnapshot(snapshot("paper-ledger.45.json"));
        assertEquals(SUCCESS, latest().status());
        assertEquals(List.of(), latest().failures());
        assertNotEquals(status.runId(), latest().runId());
        assertEquals(1, statuses.count());
    }

    private ImportResultResponse latest() {
        var statuses = importService.getImportResults("company-demo");
        assertEquals(1, statuses.size());
        return statuses.getFirst();
    }

    private Accounting snapshot(String fixture) throws IOException {
        if (fixture.startsWith("ember")) {
            return (Accounting) SnapshotHelper.asSnapshot(fixture, EmberSnapshot.class);
        }
        if (fixture.startsWith("northstar")) {
            return (Accounting) SnapshotHelper.asSnapshot(fixture, NorthstarSnapshot.class);
        }
        return (Accounting) SnapshotHelper.asSnapshot(fixture, PaperLedgerSnapshot.class);
    }
}
