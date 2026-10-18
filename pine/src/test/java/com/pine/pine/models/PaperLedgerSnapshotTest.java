package com.pine.pine.models;

import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PaperLedgerSnapshotTest {
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
