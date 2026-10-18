package com.pine.pine.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class NorthStarSnapshotTest {
    @Test
    public void reconcileSnapshot() {
        assertDoesNotThrow(() -> {
            SnapshotHelper.asSnapshot("northstar-books.7.json", NorthstarSnapshot.class);
        });
        assertDoesNotThrow(() -> {
            SnapshotHelper.asSnapshot("northstar-books.8.json", NorthstarSnapshot.class);
        });
    }
}
