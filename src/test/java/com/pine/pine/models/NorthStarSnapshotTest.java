package com.pine.pine.models;

import com.pine.pine.models.snapshots.NorthstarSnapshot;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Test
    public void preservesProviderAccountsWhenRoundTripping() throws IOException {
        var objectMapper = new ObjectMapper();
        var original = objectMapper.readTree(Path.of("src/test/resources/fixtures/northstar-books.7.json").toFile());
        var snapshot = objectMapper.treeToValue(original, NorthstarSnapshot.class);
        var json = objectMapper.readTree(objectMapper.writeValueAsString(snapshot));

        assertEquals(original, json);
        assertFalse(json.has("providerAccounts"));

        var restored = objectMapper.treeToValue(json, NorthstarSnapshot.class);
        assertEquals(snapshot.getProviderAccounts(), restored.getProviderAccounts());
        assertEquals(snapshot.getDimensions(), restored.getDimensions());
    }
}
