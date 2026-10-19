package com.pine.pine.models;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;

public class SnapshotHelper {
    public static Object asSnapshot(final String filename, final Class<?> klazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(
                Paths.get("src", "test", "resources",
                        "fixtures", filename).toFile(), klazz);
    }
}
