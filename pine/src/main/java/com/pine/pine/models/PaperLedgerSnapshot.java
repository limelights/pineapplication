package com.pine.pine.models;

import java.util.List;

public record PaperLedgerSnapshot(Source source, Export export, List<CatalogueEntry> catalogue) {

    public record Source(String system, String connection) {
    }

    public record Export(Long number, String id, String createdAt) {
    }

    public record CatalogueEntry(
            String type,
            String uid,
            String dimensionUid,
            String code,
            String displayName,
            String state) {
    }
}
