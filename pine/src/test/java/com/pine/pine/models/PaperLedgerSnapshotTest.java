package com.pine.pine.models;

import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PaperLedgerSnapshotTest {
    @Test
    public void keepsDerivedAccountsOutOfJson() throws IOException {
        var snapshot = (PaperLedgerSnapshot) SnapshotHelper.asSnapshot("paper-ledger.43.json", PaperLedgerSnapshot.class);
        var json = new ObjectMapper().valueToTree(snapshot);

        assertFalse(json.has("accounts"));
        assertEquals(5, json.get("catalogue").size());
        assertEquals("company-demo", json.get("source").get("connection").asString());
        assertEquals(43, json.get("export").get("number").asInt());
    }

    @Test
    public void reconcileSnapshot() {
        var keys = List.of(41, 42, 43, 44, 45);
        keys.forEach(k -> {
            assertDoesNotThrow(() -> {
                SnapshotHelper.asSnapshot(MessageFormat.format("paper-ledger.{0}.json", k), PaperLedgerSnapshot.class);
            });
        });
    }

    @Test
    public void paperLedgerHandlesDisplayNameObjects() {
        var keys = List.of(41, 42, 43, 44, 45);
        keys.forEach(k -> {
            assertDoesNotThrow(() -> {
                SnapshotHelper.asSnapshot(MessageFormat.format("paper-ledger.{0}.json", k), PaperLedgerSnapshot.class);
            });
        });
    }
}
