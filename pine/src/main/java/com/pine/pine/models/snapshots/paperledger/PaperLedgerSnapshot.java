package com.pine.pine.models.snapshots.paperledger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pine.pine.models.Account;
import com.pine.pine.models.AccountId;
import com.pine.pine.models.Accounting;
import com.pine.pine.models.Company;
import com.pine.pine.services.AccountAuditLogService;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public List<Account> getAccounts(final AccountAuditLogService accountAuditLogService) {
        if (catalogue == null) {
            throw new IllegalArgumentException("A complete Paperledger snapshot must include catalogue");
        }
        return catalogue.stream()
                .filter(entry -> "ACCOUNT".equals(entry.type()))
                .map(entry -> {
                    try {
                        final Account account = new Account(
                                entry.displayName().name(),
                                new AccountId(
                                        "paperledger",
                                        source.connection(),
                                        entry.uid(),
                                        getExport().number()));
                        account.setStatus(Objects.equals(entry.state, "ACTIVE"));
                        return account;
                    } catch (IllegalArgumentException e) {
                        accountAuditLogService.produceAuditLog(null, String.format("Account `%s` was not able to be ingested", entry.displayName().name()));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Company getCompany() {
        return new Company(this.getSource().connection());
    }
}
