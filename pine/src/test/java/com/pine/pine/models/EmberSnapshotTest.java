package com.pine.pine.models;

import com.pine.pine.models.snapshots.EmberSnapshot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class EmberSnapshotTest {

    @Test
    public void reconcileSnapshot() {
        assertDoesNotThrow(() -> {
            SnapshotHelper.asSnapshot("ember-accounting.9001.json", EmberSnapshot.class);
        });

        assertDoesNotThrow(() -> {
            SnapshotHelper.asSnapshot("ember-accounting.9002.json", EmberSnapshot.class);
        });
    }
}
