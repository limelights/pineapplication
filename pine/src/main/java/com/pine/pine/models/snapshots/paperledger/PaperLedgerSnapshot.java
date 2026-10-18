package com.pine.pine.models.snapshots.paperledger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pine.pine.models.Account;
import com.pine.pine.models.Accounting;
import com.pine.pine.models.Company;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public class PaperLedgerSnapshot implements Accounting {
    private Source source;
    private Export export;
    private List<CatalogueEntry> catalogue;

    public PaperLedgerSnapshot() {
    }

    public PaperLedgerSnapshot(Source source, Export export, List<CatalogueEntry> catalogue) {
        this.source = source;
        this.export = export;
        this.catalogue = catalogue;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Export getExport() {
        return export;
    }

    public void setExport(Export export) {
        this.export = export;
    }

    public List<CatalogueEntry> getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(List<CatalogueEntry> catalogue) {
        this.catalogue = catalogue;
    }

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

    @Override
    @JsonIgnore
    public List<Account> getAccounts() {
        if (catalogue == null) {
            return List.of();
        }
        return catalogue.stream()
                .filter(entry -> "ACCOUNT".equals(entry.type()))
                .map(entry -> new Account(entry.displayName().name(), entry.uid()))
                .toList();
    }

    @Override
    public Company getCompany() {
        return new Company(this.getSource().connection());
    }
}
