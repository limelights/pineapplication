package com.pine.pine.models;

import java.util.List;

public record NorthstarSnapshot(
        String provider,
        String connectionId,
        Long generation,
        String generatedAt,
        List<Account> accounts,
        List<Dimension> dimensions) {

    public record Account(String id, String code, String name, Boolean enabled) {
    }

    public record Dimension(String id, String name, List<DimensionValue> values) {
    }

    public record DimensionValue(String id, String code, String name, Boolean enabled) {
    }
}
