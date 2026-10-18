package com.pine.pine.models.paperledger;

import tools.jackson.databind.annotation.JsonDeserialize;

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
            @JsonDeserialize(using = PaperLedgerCodeDeserializer.class) String code,
            @JsonDeserialize(using = PaperLedgerDisplayNameDeserializer.class) DisplayName displayName,
            String state) {
    }

    public record DisplayName(String name, String locale) {}
}
