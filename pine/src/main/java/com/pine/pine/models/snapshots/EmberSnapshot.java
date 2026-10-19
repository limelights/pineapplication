package com.pine.pine.models.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pine.pine.models.Account;
import com.pine.pine.models.AccountId;
import com.pine.pine.models.Accounting;
import com.pine.pine.models.Company;

import java.util.List;
import java.util.Map;

public class EmberSnapshot implements Accounting {
    private String installation;
    private Long serial;
    @JsonProperty("captured_at")
    private String capturedAt;
    private Map<String, LedgerEntry> ledger;
    private Map<String, TrackingDimension> tracking;

    public EmberSnapshot() {
    }

    public EmberSnapshot(String installation, Long serial, String capturedAt,
                         Map<String, LedgerEntry> ledger, Map<String, TrackingDimension> tracking) {
        this.installation = installation;
        this.serial = serial;
        this.capturedAt = capturedAt;
        this.ledger = ledger;
        this.tracking = tracking;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public Long getSerial() {
        return serial;
    }

    public void setSerial(Long serial) {
        this.serial = serial;
    }

    public String getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(String capturedAt) {
        this.capturedAt = capturedAt;
    }

    public Map<String, LedgerEntry> getLedger() {
        return ledger;
    }

    public void setLedger(Map<String, LedgerEntry> ledger) {
        this.ledger = ledger;
    }

    public Map<String, TrackingDimension> getTracking() {
        return tracking;
    }

    public void setTracking(Map<String, TrackingDimension> tracking) {
        this.tracking = tracking;
    }

    public record LedgerEntry(
            String number,
            String title,
            @JsonProperty("is_archived") Boolean isArchived) {
    }

    public record TrackingDimension(String label, Map<String, Member> members) {
    }

    public record Member(
            String value,
            String label,
            @JsonProperty("is_archived") Boolean isArchived) {
    }

    @Override
    @JsonIgnore
    public List<Account> getAccounts() {
        if (ledger == null) {
            throw new IllegalArgumentException("A complete Ember snapshot must include ledger");
        }
        return ledger.values().stream()
                .map(entry -> {
                    var account = new Account(entry.title(), new AccountId("ember", installation, entry.number()));
                    account.setStatus(entry.isArchived());
                    return account;
                })
                .toList();
    }

    @Override
    public Company getCompany() {
        return new Company(this.installation);
    }

}
