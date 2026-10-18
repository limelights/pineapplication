package com.pine.pine.models;

import com.pine.pine.models.snapshots.EmberSnapshot;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

    @Test
    public void preservesJsonPropertiesWhenRoundTripping() throws IOException {
        var snapshot = (EmberSnapshot) SnapshotHelper.asSnapshot("ember-accounting.9002.json", EmberSnapshot.class);
        var objectMapper = new ObjectMapper();
        var json = objectMapper.valueToTree(snapshot);

        assertEquals("2026-08-04T11:00:00Z", json.get("captured_at").asString());
        assertFalse(json.has("capturedAt"));
        assertFalse(json.has("accounts"));
        assertTrue(json.get("ledger").get("cost-key").get("is_archived").asBoolean());

        var restored = objectMapper.treeToValue(json, EmberSnapshot.class);
        assertEquals("company-demo", restored.getInstallation());
        assertEquals(9002L, restored.getSerial());
        assertEquals(snapshot.getCapturedAt(), restored.getCapturedAt());
        assertEquals(snapshot.getLedger(), restored.getLedger());
        assertEquals(snapshot.getTracking(), restored.getTracking());
    }
}
